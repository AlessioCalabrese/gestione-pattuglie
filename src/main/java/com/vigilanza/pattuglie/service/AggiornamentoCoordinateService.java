package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.GeocodificaResponse;
import com.vigilanza.pattuglie.dto.PropostaIndirizzoDTO;
import com.vigilanza.pattuglie.dto.StatoAggiornamentoCoordinateDTO;
import com.vigilanza.pattuglie.dto.StatoAggiornamentoCoordinateDTO.Stato;
import com.vigilanza.pattuglie.entity.Obiettivo;
import com.vigilanza.pattuglie.repository.ObiettivoRepository;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Calcola in background le coordinate degli obiettivi attivi che ne sono privi, a partire dal loro
 * indirizzo. Gli obiettivi che hanno già le coordinate (anche corrette a mano) non vengono toccati.
 *
 * Se la via inserita non ha una corrispondenza esatta (es. errore di battitura, nome incompleto), il
 * servizio non modifica nulla da solo: registra una <em>proposta</em> con la via trovata, che l'utente
 * conferma o scarta ({@link #confermaProposta}, {@link #scartaProposta}).
 *
 * Il servizio di geocodifica (Nominatim) consente circa una richiesta al secondo, quindi con molti
 * obiettivi l'operazione dura minuti: parte in un thread dedicato e il chiamante ne segue
 * l'avanzamento con {@link #stato()}. Può girare una sola elaborazione alla volta.
 */
@Service
public class AggiornamentoCoordinateService {

    private static final Logger log = LoggerFactory.getLogger(AggiornamentoCoordinateService.class);

    /** Pausa tra due richieste consecutive, per rispettare il limite del provider di geocodifica. */
    private static final long PAUSA_TRA_RICHIESTE_MS = 1100;
    /** Dopo questi errori di rete consecutivi si interrompe: il servizio è con ogni probabilità irraggiungibile. */
    private static final int MAX_ERRORI_CONSECUTIVI = 3;
    private static final int MAX_NON_TROVATI_ELENCATI = 100;

    /** Parole che non identificano la strada (tipo di strada, preposizioni): ignorate nel confronto dei nomi. */
    private static final Set<String> PAROLE_IGNORATE = Set.of(
            "via", "viale", "vle", "corso", "piazza", "piazzale", "pzza", "pza", "largo", "vicolo", "strada", "str",
            "contrada", "cda", "localita", "loc", "lungomare", "traversa", "salita",
            "di", "del", "della", "dello", "dei", "degli", "delle", "de", "da", "al", "alla");

    /** Numero civico in coda alla via (es. "Via Roma 12", "Via Roma, 12/A"). */
    private static final Pattern CIVICO_FINALE = Pattern.compile("[\\s,]+(\\d+[a-zA-Z]?(?:\\s*/\\s*\\w+)?)\\s*$");
    /** Numeri che fanno parte del nome della strada e non sono civici (es. "Strada Statale 106"). */
    private static final Pattern NUMERO_DI_STRADA = Pattern.compile("(?i).*\\b(statale|provinciale|regionale|comunale|ss|sp|sr|km)\\s*\\d+\\s*$");

    private final ObiettivoRepository obiettivoRepository;
    private final UtenteRepository utenteRepository;
    private final GeocodingService geocodingService;
    private final LogSistemaService logSistemaService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "aggiornamento-coordinate");
        t.setDaemon(true);
        return t;
    });

    // Stato dell'ultima elaborazione (protetto da "this")
    private Stato stato = Stato.MAI_ESEGUITO;
    private int totale;
    private int elaborati;
    private int aggiornati;
    private int giaConCoordinate;
    private int errori;
    private List<String> nonTrovati = new ArrayList<>();
    private final Map<Long, PropostaIndirizzoDTO> proposte = new LinkedHashMap<>();
    private String messaggio;

    public AggiornamentoCoordinateService(ObiettivoRepository obiettivoRepository,
                                           UtenteRepository utenteRepository,
                                           GeocodingService geocodingService,
                                           LogSistemaService logSistemaService) {
        this.obiettivoRepository = obiettivoRepository;
        this.utenteRepository = utenteRepository;
        this.geocodingService = geocodingService;
        this.logSistemaService = logSistemaService;
    }

    /** Avvia l'aggiornamento e restituisce subito lo stato iniziale. Errore se ce n'è già uno in corso. */
    public synchronized StatoAggiornamentoCoordinateDTO avvia(Long utenteId, String indirizzoIp) {
        if (stato == Stato.IN_CORSO) {
            throw new IllegalArgumentException("Un aggiornamento delle coordinate è già in corso");
        }

        List<Obiettivo> attivi = obiettivoRepository.findByAttivoTrue();
        List<Long> ids = attivi.stream().filter(AggiornamentoCoordinateService::senzaCoordinate)
                .map(Obiettivo::getId).toList();

        stato = ids.isEmpty() ? Stato.COMPLETATO : Stato.IN_CORSO;
        totale = ids.size();
        elaborati = 0;
        aggiornati = 0;
        giaConCoordinate = attivi.size() - ids.size();
        errori = 0;
        nonTrovati = new ArrayList<>();
        proposte.clear();
        messaggio = ids.isEmpty() ? "Tutti gli obiettivi attivi hanno già le coordinate: niente da aggiornare." : null;

        log.info("Aggiornamento coordinate avviato (utente id={}): {} obiettivi senza coordinate su {} attivi",
                utenteId, ids.size(), attivi.size());

        if (!ids.isEmpty()) {
            executor.submit(() -> esegui(ids, utenteId, indirizzoIp));
        }
        return stato();
    }

    public synchronized StatoAggiornamentoCoordinateDTO stato() {
        return new StatoAggiornamentoCoordinateDTO(stato, totale, elaborati, aggiornati, giaConCoordinate, errori,
                List.copyOf(nonTrovati), List.copyOf(proposte.values()), messaggio);
    }

    /** Applica all'obiettivo la via e le coordinate proposte. */
    public synchronized StatoAggiornamentoCoordinateDTO confermaProposta(Long obiettivoId, Long utenteId, String indirizzoIp) {
        PropostaIndirizzoDTO proposta = proposte.get(obiettivoId);
        if (proposta == null) {
            throw new IllegalArgumentException("Proposta non trovata: è già stata confermata o scartata");
        }
        applicaProposta(proposta, utenteId, indirizzoIp);
        proposte.remove(obiettivoId);
        return stato();
    }

    /** Conferma tutte le proposte in attesa; quelle che falliscono restano in elenco. */
    public synchronized StatoAggiornamentoCoordinateDTO confermaTutte(Long utenteId, String indirizzoIp) {
        for (PropostaIndirizzoDTO proposta : new ArrayList<>(proposte.values())) {
            try {
                applicaProposta(proposta, utenteId, indirizzoIp);
                proposte.remove(proposta.getObiettivoId());
            } catch (Exception e) {
                log.warn("Proposta non applicata per l'obiettivo {}: {}", proposta.getObiettivoId(), e.getMessage());
            }
        }
        return stato();
    }

    /** Ignora la proposta: l'obiettivo resta com'è. */
    public synchronized StatoAggiornamentoCoordinateDTO scartaProposta(Long obiettivoId) {
        PropostaIndirizzoDTO scartata = proposte.remove(obiettivoId);
        if (scartata != null) {
            log.info("Proposta scartata per l'obiettivo {} '{}'", obiettivoId, scartata.getNome());
        }
        return stato();
    }

    private void applicaProposta(PropostaIndirizzoDTO proposta, Long utenteId, String indirizzoIp) {
        Obiettivo obiettivo = obiettivoRepository.findById(proposta.getObiettivoId())
                .orElseThrow(() -> new IllegalArgumentException("Obiettivo non trovato"));
        String viaPrecedente = obiettivo.getVia();
        if (proposta.getViaProposta() != null) {
            obiettivo.setVia(proposta.getViaProposta());
        }
        obiettivo.setLatitudine(proposta.getLatitudine());
        obiettivo.setLongitudine(proposta.getLongitudine());
        obiettivoRepository.save(obiettivo);

        String descrizione = "Proposta indirizzo confermata per l'obiettivo '" + obiettivo.getNome() + "' (id="
                + obiettivo.getId() + "): via '" + viaPrecedente + "' -> '" + obiettivo.getVia() + "', coordinate "
                + proposta.getLatitudine() + ", " + proposta.getLongitudine();
        log.info(descrizione);
        registraLogSistema(utenteId, "CONFERMA_INDIRIZZO", descrizione, indirizzoIp);
    }

    private void esegui(List<Long> ids, Long utenteId, String indirizzoIp) {
        int erroriConsecutivi = 0;
        boolean interrotto = false;

        for (int i = 0; i < ids.size(); i++) {
            String posizione = "[" + (i + 1) + "/" + ids.size() + "]";
            Obiettivo obiettivo = obiettivoRepository.findById(ids.get(i)).orElse(null);
            if (obiettivo == null || !senzaCoordinate(obiettivo)) { // eliminato o già sistemato nel frattempo
                log.info("{} Obiettivo id={} saltato: eliminato o con coordinate già presenti", posizione, ids.get(i));
                registraEsito(0, 0, null, null);
                continue;
            }

            String descrizioneObiettivo = "'" + obiettivo.getNome() + "' (id=" + obiettivo.getId() + ", "
                    + obiettivo.getIndirizzoCompleto() + ")";
            try {
                GeocodificaResponse trovato = cercaConVarianti(obiettivo, posizione);
                erroriConsecutivi = 0;

                if (trovato == null) {
                    log.info("{} Obiettivo {}: indirizzo NON trovato", posizione, descrizioneObiettivo);
                    registraEsito(0, 0, obiettivo.getNome() + " — " + obiettivo.getIndirizzoCompleto(), null);
                } else if (trovato.getStrada() != null && stessaVia(senzaCivico(obiettivo.getVia()), trovato.getStrada())) {
                    obiettivo.setLatitudine(trovato.getLatitudine().setScale(7, RoundingMode.HALF_UP));
                    obiettivo.setLongitudine(trovato.getLongitudine().setScale(7, RoundingMode.HALF_UP));
                    obiettivoRepository.save(obiettivo);
                    log.info("{} Obiettivo {}: coordinate aggiornate ({}, {})", posizione, descrizioneObiettivo,
                            obiettivo.getLatitudine(), obiettivo.getLongitudine());
                    registraEsito(1, 0, null, null);
                } else {
                    PropostaIndirizzoDTO proposta = creaProposta(obiettivo, trovato);
                    log.info("{} Obiettivo {}: via non corrispondente, PROPOSTA '{}' (trovato: {})", posizione,
                            descrizioneObiettivo, proposta.getViaProposta(), trovato.getIndirizzoNormalizzato());
                    registraEsito(0, 0, null, proposta);
                }
            } catch (CancellationException e) { // thread interrotto (arresto dell'applicazione)
                interrotto = true;
                break;
            } catch (Exception e) { // servizio non raggiungibile o altro errore
                log.warn("{} Obiettivo {}: errore di geocodifica: {}", posizione, descrizioneObiettivo, e.getMessage());
                registraEsito(0, 1, null, null);
                if (++erroriConsecutivi >= MAX_ERRORI_CONSECUTIVI) {
                    log.error("Aggiornamento coordinate interrotto: {} errori consecutivi", erroriConsecutivi);
                    interrotto = true;
                    break;
                }
            }

            if (i < ids.size() - 1 && !pausa()) {
                interrotto = true;
                break;
            }
        }

        termina(interrotto, utenteId, indirizzoIp);
    }

    /**
     * Cerca l'indirizzo dell'obiettivo provando, se non si trova nulla, varianti più permissive: senza il
     * civico e con il solo nome essenziale della strada (senza "Via", iniziali ecc.). Null se nessuna funziona.
     * Le richieste di rete a Nominatim (errori compresi) risalgono al chiamante.
     */
    private GeocodificaResponse cercaConVarianti(Obiettivo obiettivo, String posizione) {
        List<String> tentativi = tentativiDiRicerca(obiettivo.getVia(), obiettivo.getComune());
        for (int t = 0; t < tentativi.size(); t++) {
            try {
                log.debug("{} Ricerca '{}'", posizione, tentativi.get(t));
                return geocodingService.geocodifica(tentativi.get(t));
            } catch (IllegalArgumentException nonTrovato) {
                log.debug("{} Nessun risultato per '{}'", posizione, tentativi.get(t));
            }
            if (t < tentativi.size() - 1 && !pausa()) {
                throw new CancellationException();
            }
        }
        return null;
    }

    private static List<String> tentativiDiRicerca(String via, String comune) {
        String senzaCivico = senzaCivico(via);
        String essenziale = paroleSignificative(senzaCivico).stream().collect(Collectors.joining(" "));

        Set<String> tentativi = new LinkedHashSet<>();
        tentativi.add(via + " " + comune);
        tentativi.add(senzaCivico + " " + comune);
        if (!essenziale.isBlank()) {
            tentativi.add(essenziale + " " + comune);
        }
        return new ArrayList<>(tentativi);
    }

    private static PropostaIndirizzoDTO creaProposta(Obiettivo obiettivo, GeocodificaResponse trovato) {
        String civico = civico(obiettivo.getVia());
        String viaProposta = trovato.getStrada() == null ? null
                : trovato.getStrada() + (civico == null ? "" : " " + civico);
        return new PropostaIndirizzoDTO(obiettivo.getId(), obiettivo.getNome(), obiettivo.getVia(),
                obiettivo.getComune(), viaProposta, trovato.getIndirizzoNormalizzato(),
                trovato.getLatitudine().setScale(7, RoundingMode.HALF_UP),
                trovato.getLongitudine().setScale(7, RoundingMode.HALF_UP));
    }

    /** Vero se la via inserita e quella trovata hanno lo stesso nome, a meno di tipo di strada, iniziali e preposizioni. */
    static boolean stessaVia(String inserita, String trovata) {
        Set<String> a = paroleSignificative(inserita);
        Set<String> b = paroleSignificative(trovata);
        return !a.isEmpty() && !b.isEmpty() && (b.containsAll(a) || a.containsAll(b));
    }

    /** Parole del nome della strada in minuscolo e senza accenti, escluse iniziali, numeri e parole generiche. */
    private static Set<String> paroleSignificative(String testo) {
        String normalizzato = Normalizer.normalize(testo == null ? "" : testo.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9]+", " ");
        return Arrays.stream(normalizzato.trim().split("\\s+"))
                .filter(p -> p.length() > 1 && !PAROLE_IGNORATE.contains(p) && !p.chars().allMatch(Character::isDigit))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Numero civico in coda alla via, o null. */
    private static String civico(String via) {
        if (via == null || NUMERO_DI_STRADA.matcher(via).matches()) {
            return null;
        }
        Matcher m = CIVICO_FINALE.matcher(via);
        return m.find() ? m.group(1).trim() : null;
    }

    private static String senzaCivico(String via) {
        if (via == null || civico(via) == null) {
            return via;
        }
        return CIVICO_FINALE.matcher(via).replaceFirst("").trim();
    }

    /**
     * Vero se l'obiettivo non ha coordinate valorizzate: latitudine o longitudine assenti, oppure entrambe a 0
     * (valore predefinito del form, che non corrisponde a nessun luogo reale in Italia).
     */
    static boolean senzaCoordinate(Obiettivo o) {
        BigDecimal lat = o.getLatitudine();
        BigDecimal lng = o.getLongitudine();
        return lat == null || lng == null
                || (lat.signum() == 0 && lng.signum() == 0);
    }

    private synchronized void registraEsito(int aggiornato, int errore, String nonTrovato, PropostaIndirizzoDTO proposta) {
        elaborati++;
        aggiornati += aggiornato;
        errori += errore;
        if (nonTrovato != null && nonTrovati.size() < MAX_NON_TROVATI_ELENCATI) {
            nonTrovati.add(nonTrovato);
        }
        if (proposta != null) {
            proposte.put(proposta.getObiettivoId(), proposta);
        }
    }

    private synchronized void termina(boolean interrotto, Long utenteId, String indirizzoIp) {
        stato = interrotto ? Stato.ERRORE : Stato.COMPLETATO;
        messaggio = interrotto
                ? "Aggiornamento interrotto: il servizio di geocodifica non risponde. Riprova più tardi."
                : null;

        String riepilogo = "Aggiornamento coordinate obiettivi" + (interrotto ? " INTERROTTO" : " completato") + ": "
                + elaborati + "/" + totale + " elaborati, " + aggiornati + " aggiornati, " + proposte.size()
                + " proposte da confermare, " + giaConCoordinate + " già con coordinate, " + nonTrovati.size()
                + " non trovati, " + errori + " errori";
        log.info(riepilogo);
        registraLogSistema(utenteId, "AGGIORNA_COORDINATE", riepilogo, indirizzoIp);
    }

    private void registraLogSistema(Long utenteId, String tipoEvento, String descrizione, String indirizzoIp) {
        try {
            var utente = utenteId == null ? null : utenteRepository.findById(utenteId).orElse(null);
            logSistemaService.registra(utente, tipoEvento, descrizione, indirizzoIp);
        } catch (Exception e) {
            log.warn("Impossibile registrare l'evento {} nel log di sistema: {}", tipoEvento, e.getMessage());
        }
    }

    /** Attende tra due richieste; false se il thread è stato interrotto (arresto dell'applicazione). */
    private boolean pausa() {
        try {
            Thread.sleep(PAUSA_TRA_RICHIESTE_MS);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @PreDestroy
    void arresta() {
        executor.shutdownNow();
    }
}
