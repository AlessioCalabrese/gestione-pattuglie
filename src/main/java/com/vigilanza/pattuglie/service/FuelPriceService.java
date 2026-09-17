package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.entity.TipoCarburante;
import org.springframework.beans.factory.annotation.Value;
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
 * Il risultato è tenuto in cache per un intervallo di tempo (di default 3 ore,
 * dato che il dataset è aggiornato una volta al giorno) per non interrogare
 * il sito ad ogni richiesta di ottimizzazione rotta.
 */
@Service
public class FuelPriceService {

    @Value("${fuel-price.mimit.csv-url:https://www.mimit.gov.it/images/stories/carburanti/MediaRegionaleStradale.csv}")
    private String csvUrl;

    @Value("${fuel-price.cache-minutes:180}")
    private long cacheMinuti;

    /** Prezzo di fallback usato solo se il servizio MIMIT non è raggiungibile. */
    private static final BigDecimal PREZZO_FALLBACK_BENZINA = new BigDecimal("1.85");
    private static final BigDecimal PREZZO_FALLBACK_GASOLIO = new BigDecimal("1.75");

    private final RestClient restClient = RestClient.create();

    private volatile BigDecimal prezzoBenzinaCache;
    private volatile BigDecimal prezzoGasolioCache;
    private volatile Instant ultimoAggiornamento;

    public BigDecimal getPrezzoAlLitro(TipoCarburante tipoCarburante) {
        aggiornaCacheSeScaduta();

        BigDecimal prezzo = tipoCarburante == TipoCarburante.GASOLIO ? prezzoGasolioCache : prezzoBenzinaCache;
        if (prezzo != null) {
            return prezzo;
        }

        // Cache non disponibile (primo avvio con MIMIT irraggiungibile): fallback statico.
        return tipoCarburante == TipoCarburante.GASOLIO ? PREZZO_FALLBACK_GASOLIO : PREZZO_FALLBACK_BENZINA;
    }

    private synchronized void aggiornaCacheSeScaduta() {
        boolean cacheValida = ultimoAggiornamento != null
                && ultimoAggiornamento.plusSeconds(cacheMinuti * 60).isAfter(Instant.now());

        if (cacheValida) {
            return;
        }

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

            if (!prezziBenzina.isEmpty()) {
                prezzoBenzinaCache = media(prezziBenzina);
            }
            if (!prezziGasolio.isEmpty()) {
                prezzoGasolioCache = media(prezziGasolio);
            }
            ultimoAggiornamento = Instant.now();

        } catch (Exception e) {
            // Se il sito MIMIT non è raggiungibile, si continua a usare l'ultimo
            // valore in cache (anche se scaduto) piuttosto che fallire la richiesta;
            // solo se non c'è mai stata una cache valida si ricade sul fallback statico.
            if (ultimoAggiornamento == null) {
                ultimoAggiornamento = Instant.now();
            }
        }
    }

    private BigDecimal media(List<BigDecimal> valori) {
        BigDecimal somma = valori.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return somma.divide(BigDecimal.valueOf(valori.size()), 3, RoundingMode.HALF_UP);
    }
}
