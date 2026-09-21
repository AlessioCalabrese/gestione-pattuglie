package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.FlagObiettivoRequest;
import com.vigilanza.pattuglie.dto.NuovoObiettivoRequest;
import com.vigilanza.pattuglie.dto.ObiettivoDTO;
import com.vigilanza.pattuglie.dto.OttimizzazioneRottaResponse;
import com.vigilanza.pattuglie.entity.*;
import com.vigilanza.pattuglie.repository.ObiettivoFlagRepository;
import com.vigilanza.pattuglie.repository.ObiettivoRepository;
import com.vigilanza.pattuglie.repository.PattugliaRepository;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ObiettivoService {

    private final ObiettivoRepository obiettivoRepository;
    private final ObiettivoFlagRepository obiettivoFlagRepository;
    private final PattugliaRepository pattugliaRepository;
    private final UtenteRepository utenteRepository;
    private final RouteOptimizerService routeOptimizerService;
    private final LogSistemaService logSistemaService;
    private final FuelPriceService fuelPriceService;

    private static final DateTimeFormatter FORMATO_ORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_MESSAGGIO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_ORA_MESSAGGIO = DateTimeFormatter.ofPattern("HH:mm");

    public ObiettivoService(ObiettivoRepository obiettivoRepository,
                             ObiettivoFlagRepository obiettivoFlagRepository,
                             PattugliaRepository pattugliaRepository,
                             UtenteRepository utenteRepository,
                             RouteOptimizerService routeOptimizerService,
                             LogSistemaService logSistemaService,
                             FuelPriceService fuelPriceService) {
        this.obiettivoRepository = obiettivoRepository;
        this.obiettivoFlagRepository = obiettivoFlagRepository;
        this.pattugliaRepository = pattugliaRepository;
        this.utenteRepository = utenteRepository;
        this.routeOptimizerService = routeOptimizerService;
        this.logSistemaService = logSistemaService;
        this.fuelPriceService = fuelPriceService;
    }

    public List<ObiettivoDTO> findByPattuglia(Long pattugliaId) {
        List<Obiettivo> obiettivi = obiettivoRepository
                .findByPattugliaIdAndAttivoTrueOrderByOrdineVisitaAsc(pattugliaId);

        GiornoSettimana oggi = GiornoSettimana.daDayOfWeek(LocalDate.now().getDayOfWeek());

        // Un obiettivo è visibile alla pattuglia solo se il giorno odierno è tra
        // quelli esplicitamente configurati. Un obiettivo senza alcun giorno
        // impostato non è mai visibile (la pianificazione va sempre indicata).
        return obiettivi.stream()
                .filter(o -> o.getGiorniAttivi().contains(oggi))
                .map(this::toDtoConStatoFlag)
                .toList();
    }

    public Obiettivo crea(Long pattugliaId, NuovoObiettivoRequest request) {
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));

        Obiettivo obiettivo = new Obiettivo();
        obiettivo.setPattuglia(pattuglia);
        applicaRichiesta(obiettivo, request);

        return obiettivoRepository.save(obiettivo);
    }

    /** Tutti gli obiettivi attivi della pattuglia, indipendentemente dai giorni di servizio (vista amministratore). */
    public List<ObiettivoDTO> findPerAdmin(Long pattugliaId) {
        return obiettivoRepository.findByPattugliaIdAndAttivoTrueOrderByOrdineVisitaAsc(pattugliaId).stream()
                .map(this::toDtoConStatoFlag)
                .toList();
    }

    /** Aggiorna i dati di un obiettivo esistente (indirizzo, pianificazione, telefono...). Lo storico dei flag resta invariato. */
    public Obiettivo aggiorna(Long obiettivoId, NuovoObiettivoRequest request) {
        Obiettivo obiettivo = obiettivoRepository.findById(obiettivoId)
                .orElseThrow(() -> new IllegalArgumentException("Obiettivo non trovato"));
        applicaRichiesta(obiettivo, request);
        return obiettivoRepository.save(obiettivo);
    }

    /** Copia sull'obiettivo i campi modificabili della richiesta, validandoli (usata da creazione e modifica). */
    private void applicaRichiesta(Obiettivo obiettivo, NuovoObiettivoRequest request) {
        if (request.getGiorniAttivi() == null || request.getGiorniAttivi().isEmpty()) {
            throw new IllegalArgumentException(
                    "Seleziona almeno un giorno di servizio: un obiettivo senza giorni configurati non sarebbe mai visibile alle pattuglie.");
        }

        obiettivo.setNome(request.getNome());
        obiettivo.setVia(request.getVia());
        obiettivo.setNumeroCivico(request.getNumeroCivico());
        obiettivo.setComune(request.getComune());
        obiettivo.setLatitudine(request.getLatitudine());
        obiettivo.setLongitudine(request.getLongitudine());
        obiettivo.setPriorita(request.isPriorita());
        obiettivo.setTelefonoRiferimento(normalizzaTelefono(request.getTelefonoRiferimento()));

        obiettivo.getGiorniAttivi().clear();
        obiettivo.getGiorniAttivi().addAll(request.getGiorniAttivi());
        obiettivo.setOraInizio(request.getOraInizio());
        obiettivo.setOraFine(request.getOraFine());
        if (request.getRipetizioniGiornaliere() != null && request.getRipetizioniGiornaliere() > 0) {
            obiettivo.setRipetizioniGiornaliere(request.getRipetizioniGiornaliere());
        }
    }

    /**
     * Registra il passaggio su un obiettivo. Inserisce sempre un nuovo record
     * (mai un update), permettendo più flag nello stesso giorno.
     */
    public void flagObiettivo(Long utenteId, Long pattugliaId, FlagObiettivoRequest request, String indirizzoIp) {
        Obiettivo obiettivo = obiettivoRepository.findById(request.getObiettivoId())
                .orElseThrow(() -> new IllegalArgumentException("Obiettivo non trovato"));

        if (!obiettivo.getPattuglia().getId().equals(pattugliaId)) {
            throw new IllegalArgumentException("L'obiettivo non appartiene alla pattuglia selezionata");
        }

        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        ObiettivoFlag flag = new ObiettivoFlag(obiettivo, utente, obiettivo.getPattuglia(),
                request.getLatitudine(), request.getLongitudine());
        flag.setPrecisioneMetri(request.getPrecisioneMetri());
        flag.setNote(request.getNote());

        obiettivoFlagRepository.save(flag);

        logSistemaService.registra(utente, "FLAG_OBIETTIVO",
                "Flag su obiettivo '" + obiettivo.getNome() + "' (id=" + obiettivo.getId() + ")",
                indirizzoIp);
    }

    /**
     * Consumo medio generico usato per stimare litri/costo risparmiati, dato che
     * il sistema non registra il consumo specifico del veicolo di ciascuna
     * pattuglia. È una media indicativa per un veicolo di media cilindrata in
     * uso urbano/extraurbano misto — se in futuro si vorrà una stima più
     * precisa, andrà associato un consumo reale al singolo veicolo.
     */
    private static final BigDecimal CONSUMO_MEDIO_GENERICO_L_100KM = new BigDecimal("8.00");

    /**
     * Calcola e salva l'ordine di visita ottimale degli obiettivi attivi
     * di una pattuglia, a partire dalla posizione corrente fornita
     * (es. rilevata dal tablet della pattuglia, o sede di partenza).
     * Restituisce anche la stima del risparmio in km, litri e costo carburante
     * rispetto all'ordine con cui gli obiettivi erano elencati prima del calcolo.
     */
    public OttimizzazioneRottaResponse ottimizzaRotta(Long pattugliaId, double latPartenza, double lngPartenza) {
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));

        List<Obiettivo> tuttiGliObiettiviAttivi = obiettivoRepository
                .findByPattugliaIdAndAttivoTrueOrderByOrdineVisitaAsc(pattugliaId);

        LocalDate oggi = LocalDate.now();
        LocalTime adesso = LocalTime.now();
        GiornoSettimana giornoOggi = GiornoSettimana.daDayOfWeek(oggi.getDayOfWeek());

        // Un obiettivo è considerato "di oggi" solo se il giorno odierno è tra
        // quelli esplicitamente configurati; senza alcun giorno impostato non
        // compare mai (né in lista, né nel percorso).
        List<Obiettivo> obiettiviDiOggi = tuttiGliObiettiviAttivi.stream()
                .filter(o -> o.getGiorniAttivi().contains(giornoOggi))
                .toList();

        // Tra quelli di oggi, solo chi rientra anche nella fascia oraria pianificata
        // entra nel calcolo del percorso; gli altri restano visibili ma marcati
        // "fuori servizio" (es. il loro turno inizia più tardi nella stessa giornata).
        List<Obiettivo> obiettiviInServizio = new ArrayList<>();
        List<Obiettivo> obiettiviFuoriServizio = new ArrayList<>();
        for (Obiettivo o : obiettiviDiOggi) {
            (o.isInServizio(oggi, adesso) ? obiettiviInServizio : obiettiviFuoriServizio).add(o);
        }

        // Gli obiettivi esclusi non fanno parte del percorso odierno: azzeriamo
        // il loro ordine di visita per non lasciare numeri residui di ottimizzazioni
        // precedenti, che confonderebbero l'ordinamento della lista.
        for (Obiettivo o : obiettiviFuoriServizio) {
            o.setOrdineVisita(null);
        }
        if (!obiettiviFuoriServizio.isEmpty()) {
            obiettivoRepository.saveAll(obiettiviFuoriServizio);
        }

        // Copia dell'ordine attuale (prima dell'ottimizzazione), per il confronto.
        List<Obiettivo> ordineOriginale = new ArrayList<>(obiettiviInServizio);
        double distanzaOriginale = routeOptimizerService.calcolaLunghezzaKm(latPartenza, lngPartenza, ordineOriginale);

        List<Obiettivo> ottimizzati = routeOptimizerService.ottimizzaRotta(latPartenza, lngPartenza, obiettiviInServizio);
        obiettivoRepository.saveAll(ottimizzati);
        double distanzaOttimizzata = routeOptimizerService.calcolaLunghezzaKm(latPartenza, lngPartenza, ottimizzati);

        BigDecimal prezzoAlLitro = fuelPriceService.getPrezzoAlLitro(pattuglia.getTipoCarburante());

        BigDecimal litriOriginali = stimaLitri(distanzaOriginale, CONSUMO_MEDIO_GENERICO_L_100KM);
        BigDecimal litriOttimizzati = stimaLitri(distanzaOttimizzata, CONSUMO_MEDIO_GENERICO_L_100KM);
        BigDecimal risparmioLitri = litriOriginali.subtract(litriOttimizzati).max(BigDecimal.ZERO);
        BigDecimal risparmioCosto = risparmioLitri.multiply(prezzoAlLitro).setScale(2, RoundingMode.HALF_UP);

        double risparmioKm = Math.max(0, distanzaOriginale - distanzaOttimizzata);
        double risparmioPercentuale = distanzaOriginale > 0 ? (risparmioKm / distanzaOriginale) * 100.0 : 0.0;

        // La lista restituita mostra prima il percorso ottimizzato di oggi, poi
        // gli obiettivi fuori pianificazione (senza ordine di visita, ma visibili
        // e marcati "fuori servizio" per trasparenza verso la pattuglia).
        List<ObiettivoDTO> obiettiviRisposta = new ArrayList<>();
        obiettiviRisposta.addAll(ottimizzati.stream().map(this::toDtoConStatoFlag).toList());
        obiettiviRisposta.addAll(obiettiviFuoriServizio.stream().map(this::toDtoConStatoFlag).toList());

        OttimizzazioneRottaResponse response = new OttimizzazioneRottaResponse();
        response.setObiettivi(obiettiviRisposta);
        response.setObiettiviInServizioOggi(ottimizzati.size());
        response.setDistanzaOriginaleKm(arrotonda(distanzaOriginale));
        response.setDistanzaOttimizzataKm(arrotonda(distanzaOttimizzata));
        response.setRisparmioKm(arrotonda(risparmioKm));
        response.setRisparmioPercentuale(arrotonda(risparmioPercentuale));
        response.setLitriStimatiOriginali(litriOriginali);
        response.setLitriStimatiOttimizzati(litriOttimizzati);
        response.setRisparmioLitri(risparmioLitri);
        response.setRisparmioCosto(risparmioCosto);
        response.setPrezzoCarburanteAlLitro(prezzoAlLitro);

        return response;
    }

    private BigDecimal stimaLitri(double km, BigDecimal consumoL100Km) {
        return consumoL100Km.multiply(BigDecimal.valueOf(km))
                .divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);
    }

    private double arrotonda(double valore) {
        return Math.round(valore * 100.0) / 100.0;
    }

    private ObiettivoDTO toDtoConStatoFlag(Obiettivo o) {
        ObiettivoDTO dto = new ObiettivoDTO(o.getId(), o.getPattuglia().getId(), o.getNome(),
                o.getVia(), o.getNumeroCivico(), o.getComune(), o.getLatitudine(), o.getLongitudine(),
                o.getOrdineVisita(), o.isAttivo());
        dto.setPriorita(o.isPriorita());
        dto.setGiorniAttivi(o.getGiorniAttivi());
        dto.setOraInizio(o.getOraInizio());
        dto.setOraFine(o.getOraFine());
        dto.setRipetizioniGiornaliere(o.getRipetizioniGiornaliere());

        LocalDateTime adesso = LocalDateTime.now();
        dto.setInServizioOra(o.isInServizio(adesso.toLocalDate(), adesso.toLocalTime()));

        LocalDateTime inizioGiorno = LocalDate.now().atStartOfDay();
        LocalDateTime fineGiorno = inizioGiorno.plusDays(1);

        List<ObiettivoFlag> flagOggi = obiettivoFlagRepository
                .findByObiettivoIdAndDataOraBetween(o.getId(), inizioGiorno, fineGiorno);

        dto.setFlaggatoOggi(!flagOggi.isEmpty());
        dto.setNumeroFlagOggi(flagOggi.size());
        dto.setCompletatoOggi(flagOggi.size() >= o.getRipetizioniGiornaliere());

        Optional<ObiettivoFlag> ultimo = flagOggi.stream()
                .max((a, b) -> a.getDataOra().compareTo(b.getDataOra()));
        ultimo.ifPresent(f -> dto.setUltimoFlagDataOra(f.getDataOra().format(FORMATO_ORA)));

        dto.setTelefonoRiferimento(o.getTelefonoRiferimento());
        if (o.getTelefonoRiferimento() != null) {
            ultimo.ifPresent(f -> dto.setWhatsappUrl(costruisciLinkWhatsapp(o, f.getDataOra())));
        }

        return dto;
    }

    /**
     * Porta il numero inserito dall'amministratore in formato internazionale di sole cifre
     * (quello richiesto da wa.me). Accetta spazi, punti, trattini, "+39", "0039" e i cellulari
     * italiani scritti senza prefisso. Vuoto = nessun telefono (null).
     */
    static String normalizzaTelefono(String grezzo) {
        if (grezzo == null || grezzo.isBlank()) {
            return null;
        }
        String cifre = grezzo.replaceAll("[\\s.\\-()/]", "");
        if (cifre.startsWith("+")) {
            cifre = cifre.substring(1);
        } else if (cifre.startsWith("00")) {
            cifre = cifre.substring(2);
        } else if (cifre.startsWith("3") && (cifre.length() == 9 || cifre.length() == 10)) {
            cifre = "39" + cifre; // cellulare italiano senza prefisso
        }
        if (!cifre.matches("\\d{8,15}") || cifre.startsWith("0")) {
            throw new IllegalArgumentException(
                    "Numero di telefono non valido: inserisci un cellulare, con prefisso internazionale se non italiano (es. +39 333 1234567).");
        }
        return cifre;
    }

    /** Link "click to chat" con il messaggio per l'obiettivo, che la pattuglia invia dal proprio WhatsApp. */
    private String costruisciLinkWhatsapp(Obiettivo o, LocalDateTime oraCheck) {
        String messaggio = "Gentile cliente, la informiamo che la pattuglia COSMOPOL ha effettuato il controllo presso "
                + o.getNome() + " il " + oraCheck.format(FORMATO_DATA_MESSAGGIO)
                + " alle ore " + oraCheck.format(FORMATO_ORA_MESSAGGIO) + ". "
                + "La ringraziamo per aver scelto il servizio COSMOPOL.";
        return "https://wa.me/" + o.getTelefonoRiferimento()
                + "?text=" + URLEncoder.encode(messaggio, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
