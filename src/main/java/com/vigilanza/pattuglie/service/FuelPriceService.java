package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.entity.TipoCarburante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Recupera il prezzo medio nazionale del carburante direttamente da internet,
 * dal dataset open-data ufficiale del Ministero delle Imprese e del Made in
 * Italy (MIMIT), pubblicato quotidianamente in formato CSV a un URL fisso:
 * https://www.mimit.gov.it/images/stories/carburanti/MediaRegionaleStradale.csv
 *
 * Il file contiene, per ogni regione/provincia autonoma, il prezzo medio
 * "self service" di benzina e gasolio rilevato alle ore 8 del mattino. Qui
 * calcoliamo la media aritmetica nazionale sulle righe di tipo Benzina o
 * Gasolio, secondo il tipo di carburante configurato per la pattuglia.
 *
 * Il valore viene aggiornato automaticamente ogni notte all'1:00 tramite job
 * schedulato ({@link #aggiornamentoNotturno()}), che è l'orario in cui il
 * dataset del giorno precedente è sicuramente stabile (il Ministero pubblica
 * i nuovi dati entro le 8:30 del mattino). In aggiunta, se per qualsiasi
 * motivo la cache risultasse scaduta o assente (es. subito dopo un riavvio,
 * prima ancora che scatti il job notturno), il valore viene comunque
 * ricalcolato al volo alla prima richiesta utile.
 */
@Service
public class FuelPriceService {

    private static final Logger log = LoggerFactory.getLogger(FuelPriceService.class);

    @Value("${fuel-price.mimit.csv-url:https://www.mimit.gov.it/images/stories/carburanti/MediaRegionaleStradale.csv}")
    private String csvUrl;

    @Value("${fuel-price.cache-minutes:180}")
    private long cacheMinuti;

    /** Prezzo di fallback usato solo se il servizio MIMIT non è mai stato raggiungibile. */
    private static final BigDecimal PREZZO_FALLBACK_BENZINA = new BigDecimal("1.85");
    private static final BigDecimal PREZZO_FALLBACK_GASOLIO = new BigDecimal("1.75");

    private final RestClient restClient = RestClient.create();

    private volatile BigDecimal prezzoBenzinaCache;
    private volatile BigDecimal prezzoGasolioCache;
    private volatile Instant ultimoAggiornamento;
    private volatile boolean ultimoAggiornamentoRiuscito = false;

    public BigDecimal getPrezzoAlLitro(TipoCarburante tipoCarburante) {
        aggiornaSeNecessario();

        BigDecimal prezzo = tipoCarburante == TipoCarburante.GASOLIO ? prezzoGasolioCache : prezzoBenzinaCache;
        if (prezzo != null) {
            return prezzo;
        }

        // Cache non disponibile (primo avvio con MIMIT irraggiungibile): fallback statico.
        return tipoCarburante == TipoCarburante.GASOLIO ? PREZZO_FALLBACK_GASOLIO : PREZZO_FALLBACK_BENZINA;
    }

    /**
     * Job schedulato: ogni notte all'1:00 (ora del server) forza il
     * ricalcolo del prezzo medio dal dataset MIMIT, indipendentemente
     * da quando è avvenuto l'ultimo aggiornamento.
     */
    @Scheduled(cron = "0 0 1 * * *", zone = "Europe/Rome")
    public void aggiornamentoNotturno() {
        log.info("Aggiornamento notturno prezzo carburante da MIMIT in corso...");
        boolean riuscito = aggiornaCache();
        if (riuscito) {
            log.info("Prezzo carburante aggiornato: benzina={}, gasolio={}", prezzoBenzinaCache, prezzoGasolioCache);
        } else {
            log.warn("Aggiornamento notturno prezzo carburante fallito: dataset MIMIT non raggiungibile o formato inatteso. "
                    + "Verrà mantenuto l'ultimo valore disponibile in cache.");
        }
    }

    /** Aggiorna la cache solo se scaduta (usato come fallback lazy, non dal job schedulato). */
    private synchronized void aggiornaSeNecessario() {
        boolean cacheValida = ultimoAggiornamento != null
                && ultimoAggiornamento.plusSeconds(cacheMinuti * 60).isAfter(Instant.now());

        if (!cacheValida) {
            aggiornaCache();
        }
    }

    /** Esegue il fetch e il parsing del CSV. Restituisce true se l'aggiornamento è andato a buon fine. */
    private synchronized boolean aggiornaCache() {
        try {
            String csv = restClient.get().uri(csvUrl).retrieve().body(String.class);
            if (csv == null || csv.isBlank()) {
                throw new IllegalStateException("Risposta CSV vuota");
            }

            List<BigDecimal> prezziBenzina = new ArrayList<>();
            List<BigDecimal> prezziGasolio = new ArrayList<>();

            for (String riga : csv.split("\\R")) {
                String[] campi = riga.split(";");
                // Formato atteso: REGIONE;TIPOLOGIA;EROGAZIONE;PREZZO MEDIO
                if (campi.length < 4) {
                    continue;
                }
                String tipologia = campi[1].trim();
                String prezzoTesto = campi[3].trim();

                try {
                    BigDecimal prezzo = new BigDecimal(prezzoTesto.replace(",", "."));
                    if ("Benzina".equalsIgnoreCase(tipologia)) {
                        prezziBenzina.add(prezzo);
                    } else if ("Gasolio".equalsIgnoreCase(tipologia)) {
                        prezziGasolio.add(prezzo);
                    }
                } catch (NumberFormatException ignorato) {
                    // riga di intestazione, dato non disponibile, o riga vuota: salta
                }
            }

            if (prezziBenzina.isEmpty() && prezziGasolio.isEmpty()) {
                throw new IllegalStateException("Nessun prezzo valido trovato nel CSV (formato cambiato?)");
            }

            if (!prezziBenzina.isEmpty()) {
                prezzoBenzinaCache = media(prezziBenzina);
            }
            if (!prezziGasolio.isEmpty()) {
                prezzoGasolioCache = media(prezziGasolio);
            }
            ultimoAggiornamento = Instant.now();
            ultimoAggiornamentoRiuscito = true;
            return true;

        } catch (Exception e) {
            log.warn("Impossibile recuperare il prezzo carburante da MIMIT: {}", e.getMessage());
            // Se il sito MIMIT non è raggiungibile, si continua a usare l'ultimo
            // valore in cache (anche se scaduto) piuttosto che fallire la richiesta;
            // solo se non c'è mai stata una cache valida si ricade sul fallback statico.
            if (ultimoAggiornamento == null) {
                ultimoAggiornamento = Instant.now();
            }
            ultimoAggiornamentoRiuscito = false;
            return false;
        }
    }

    private BigDecimal media(List<BigDecimal> valori) {
        BigDecimal somma = valori.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return somma.divide(BigDecimal.valueOf(valori.size()), 3, RoundingMode.HALF_UP);
    }
}
