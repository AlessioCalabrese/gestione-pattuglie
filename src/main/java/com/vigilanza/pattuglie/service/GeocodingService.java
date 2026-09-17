package com.vigilanza.pattuglie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vigilanza.pattuglie.dto.GeocodificaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Geocodifica un indirizzo in coordinate lat/long.
 *
 * Implementazione di default: Nominatim (OpenStreetMap), gratuito e senza
 * API key, ma con policy d'uso che richiede un header User-Agent valido e un
 * limite di ~1 richiesta al secondo (adeguato per l'uso "on demand" da form
 * di amministrazione, non per geocodifiche massive/batch).
 *
 * Per volumi più alti o maggiore precisione su indirizzi italiani, sostituire
 * l'implementazione con Google Geocoding API o un provider a pagamento
 * mantenendo invariata la firma di geocodifica(...).
 */
@Service
public class GeocodingService {

    @Value("${geocoding.nominatim.base-url:https://nominatim.openstreetmap.org}")
    private String baseUrl;

    @Value("${geocoding.nominatim.user-agent:GestionePattuglieApp/1.0 (contatto@istituto-vigilanza.it)}")
    private String userAgent;

    @Value("${geocoding.paese-default:it}")
    private String paeseDefault;

    private final RestClient restClient = RestClient.create();

    public GeocodificaResponse geocodifica(String indirizzo) {
        if (indirizzo == null || indirizzo.isBlank()) {
            throw new IllegalArgumentException("Indirizzo non valido");
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search")
                .queryParam("q", indirizzo)
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .queryParam("countrycodes", paeseDefault)
                .queryParam("addressdetails", 1)
                .build()
                .toUri();

        JsonNode[] risultati;
        try {
            risultati = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.USER_AGENT, userAgent)
                    .retrieve()
                    .body(JsonNode[].class);
        } catch (Exception e) {
            throw new IllegalStateException("Servizio di geocodifica non raggiungibile: " + e.getMessage());
        }

        if (risultati == null || risultati.length == 0) {
            throw new IllegalArgumentException("Nessun risultato trovato per l'indirizzo indicato");
        }

        JsonNode primo = risultati[0];
        BigDecimal lat = new BigDecimal(primo.get("lat").asText());
        BigDecimal lon = new BigDecimal(primo.get("lon").asText());
        String indirizzoNormalizzato = primo.has("display_name") ? primo.get("display_name").asText() : indirizzo;

        return new GeocodificaResponse(lat, lon, indirizzoNormalizzato);
    }
}
