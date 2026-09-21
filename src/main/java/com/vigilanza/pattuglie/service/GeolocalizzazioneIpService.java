package com.vigilanza.pattuglie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vigilanza.pattuglie.dto.PosizioneDTO;
import com.vigilanza.pattuglie.dto.PosizioneDTO.Fonte;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Stima la posizione di un client dal suo indirizzo IP pubblico, quando il GPS non è disponibile.
 *
 * È una stima grossolana (di norma il livello di città, a volte sbagliata di decine di km con reti mobili
 * o VPN), quindi non prova la presenza fisica in un punto preciso: chi la usa deve registrarla come tale.
 * Se l'IP del client è interno (rete locale, VPN aziendale) non è geolocalizzabile: si ripiega sulla sede
 * di riferimento configurata, se presente.
 */
@Service
public class GeolocalizzazioneIpService {

    private static final Pattern IPV4 = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");
    private static final Pattern IPV6 = Pattern.compile("^[0-9a-fA-F:.]+$");

    @Value("${geolocalizzazione.ip.base-url:https://ipwho.is}")
    private String baseUrl;

    @Value("${geolocalizzazione.ip.precisione-metri:10000}")
    private int precisioneMetri;

    @Value("${geolocalizzazione.sede.latitudine:}")
    private String sedeLatitudine;

    @Value("${geolocalizzazione.sede.longitudine:}")
    private String sedeLongitudine;

    @Value("${geolocalizzazione.sede.precisione-metri:20000}")
    private int sedePrecisioneMetri;

    private final RestClient restClient = creaRestClient();

    private static RestClient creaRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return RestClient.builder().requestFactory(factory).build();
    }

    /**
     * Stima la posizione del client. "inoltrati" sono gli indirizzi della catena X-Forwarded-For (se il server
     * è dietro un proxy), "diretto" quello della connessione: si usa il primo indirizzo pubblico tra i due.
     */
    public PosizioneDTO stima(String inoltrati, String diretto) {
        String ip = primoIndirizzoPubblico(inoltrati, diretto);

        if (ip == null) { // rete interna: non geolocalizzabile
            return posizioneSede().orElseThrow(() -> new IllegalArgumentException(
                    "Posizione non determinabile dalla rete: il dispositivo è su una rete interna e non è "
                            + "configurata una sede di riferimento"));
        }

        JsonNode risposta;
        try {
            risposta = restClient.get().uri(baseUrl + "/" + ip).retrieve().body(JsonNode.class);
        } catch (Exception e) {
            throw new IllegalStateException("Servizio di geolocalizzazione da IP non raggiungibile: " + e.getMessage());
        }

        if (risposta == null || !risposta.path("success").asBoolean(true)
                || !risposta.hasNonNull("latitude") || !risposta.hasNonNull("longitude")) {
            throw new IllegalStateException("Il servizio di geolocalizzazione non ha trovato l'indirizzo del dispositivo");
        }

        String citta = risposta.path("city").asText("");
        String regione = risposta.path("region").asText("");
        String localita = (citta + (citta.isEmpty() || regione.isEmpty() ? "" : ", ") + regione).trim();

        return new PosizioneDTO(risposta.get("latitude").asDouble(), risposta.get("longitude").asDouble(),
                precisioneMetri, Fonte.RETE, localita.isEmpty() ? null : localita);
    }

    private java.util.Optional<PosizioneDTO> posizioneSede() {
        try {
            if (sedeLatitudine.isBlank() || sedeLongitudine.isBlank()) {
                return java.util.Optional.empty();
            }
            return java.util.Optional.of(new PosizioneDTO(Double.parseDouble(sedeLatitudine.trim()),
                    Double.parseDouble(sedeLongitudine.trim()), sedePrecisioneMetri, Fonte.SEDE, "sede di riferimento"));
        } catch (NumberFormatException e) {
            return java.util.Optional.empty();
        }
    }

    private static String primoIndirizzoPubblico(String inoltrati, String diretto) {
        if (inoltrati != null) {
            for (String candidato : inoltrati.split(",")) {
                if (isPubblico(candidato.trim())) {
                    return candidato.trim();
                }
            }
        }
        return diretto != null && isPubblico(diretto.trim()) ? diretto.trim() : null;
    }

    /** Vero se è un indirizzo IP letterale (mai un nome: niente risoluzioni DNS) e non è interno. */
    static boolean isPubblico(String indirizzo) {
        if (indirizzo == null || !(IPV4.matcher(indirizzo).matches()
                || (indirizzo.contains(":") && IPV6.matcher(indirizzo).matches()))) {
            return false;
        }
        try {
            InetAddress ip = InetAddress.getByName(indirizzo); // letterale: nessuna query DNS
            byte[] byteIp = ip.getAddress();
            boolean uniqueLocalIpv6 = byteIp.length == 16 && (byteIp[0] & 0xFE) == 0xFC; // fc00::/7
            boolean cgnat = byteIp.length == 4 && (byteIp[0] & 0xFF) == 100
                    && (byteIp[1] & 0xC0) == 0x40; // 100.64.0.0/10
            return !(ip.isAnyLocalAddress() || ip.isLoopbackAddress() || ip.isLinkLocalAddress()
                    || ip.isSiteLocalAddress() || ip.isMulticastAddress() || uniqueLocalIpv6 || cgnat);
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
