package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.FlagObiettivoRequest;
import com.vigilanza.pattuglie.dto.ObiettivoDTO;
import com.vigilanza.pattuglie.dto.OttimizzazioneRottaResponse;
import com.vigilanza.pattuglie.entity.*;
import com.vigilanza.pattuglie.repository.ObiettivoFlagRepository;
import com.vigilanza.pattuglie.repository.ObiettivoRepository;
import com.vigilanza.pattuglie.repository.PattugliaRepository;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return obiettivi.stream().map(this::toDtoConStatoFlag).toList();
    }

    public Obiettivo crea(Long pattugliaId, String nome, String indirizzo,
                           java.math.BigDecimal lat, java.math.BigDecimal lng) {
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));
        Obiettivo obiettivo = new Obiettivo(pattuglia, nome, indirizzo, lat, lng);
        return obiettivoRepository.save(obiettivo);
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
     * Calcola e salva l'ordine di visita ottimale degli obiettivi attivi
     * di una pattuglia, a partire dalla posizione corrente fornita
     * (es. rilevata dal tablet della pattuglia, o sede di partenza).
     * Restituisce anche la stima del risparmio in km, litri e costo carburante
     * rispetto all'ordine con cui gli obiettivi erano elencati prima del calcolo.
     */
    public OttimizzazioneRottaResponse ottimizzaRotta(Long pattugliaId, double latPartenza, double lngPartenza) {
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));

        List<Obiettivo> obiettiviOrdineAttuale = obiettivoRepository
                .findByPattugliaIdAndAttivoTrueOrderByOrdineVisitaAsc(pattugliaId);

        // Copia dell'ordine attuale (prima dell'ottimizzazione), per il confronto.
        List<Obiettivo> ordineOriginale = new ArrayList<>(obiettiviOrdineAttuale);
        double distanzaOriginale = routeOptimizerService.calcolaLunghezzaKm(latPartenza, lngPartenza, ordineOriginale);

        List<Obiettivo> ottimizzati = routeOptimizerService.ottimizzaRotta(latPartenza, lngPartenza, obiettiviOrdineAttuale);
        obiettivoRepository.saveAll(ottimizzati);
        double distanzaOttimizzata = routeOptimizerService.calcolaLunghezzaKm(latPartenza, lngPartenza, ottimizzati);

        BigDecimal consumoL100Km = pattuglia.getConsumoMedioL100Km() != null
                ? pattuglia.getConsumoMedioL100Km() : new BigDecimal("10.00");
        BigDecimal prezzoAlLitro = fuelPriceService.getPrezzoAlLitro(pattuglia.getTipoCarburante());

        BigDecimal litriOriginali = stimaLitri(distanzaOriginale, consumoL100Km);
        BigDecimal litriOttimizzati = stimaLitri(distanzaOttimizzata, consumoL100Km);
        BigDecimal risparmioLitri = litriOriginali.subtract(litriOttimizzati).max(BigDecimal.ZERO);
        BigDecimal risparmioCosto = risparmioLitri.multiply(prezzoAlLitro).setScale(2, RoundingMode.HALF_UP);

        double risparmioKm = Math.max(0, distanzaOriginale - distanzaOttimizzata);
        double risparmioPercentuale = distanzaOriginale > 0 ? (risparmioKm / distanzaOriginale) * 100.0 : 0.0;

        OttimizzazioneRottaResponse response = new OttimizzazioneRottaResponse();
        response.setObiettivi(ottimizzati.stream().map(this::toDtoConStatoFlag).toList());
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
                o.getIndirizzo(), o.getLatitudine(), o.getLongitudine(),
                o.getOrdineVisita(), o.isAttivo());

        LocalDateTime inizioGiorno = LocalDate.now().atStartOfDay();
        LocalDateTime fineGiorno = inizioGiorno.plusDays(1);

        List<ObiettivoFlag> flagOggi = obiettivoFlagRepository
                .findByObiettivoIdAndDataOraBetween(o.getId(), inizioGiorno, fineGiorno);

        dto.setFlaggatoOggi(!flagOggi.isEmpty());

        Optional<ObiettivoFlag> ultimo = flagOggi.stream()
                .max((a, b) -> a.getDataOra().compareTo(b.getDataOra()));
        ultimo.ifPresent(f -> dto.setUltimoFlagDataOra(f.getDataOra().format(FORMATO_ORA)));

        return dto;
    }
}
