package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.config.WhatsappProperties;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Si occupa dei messaggi WhatsApp verso gli obiettivi: normalizza i numeri, compone il testo a
 * partire dal template configurato (vedi {@link WhatsappProperties}) e costruisce il link
 * "click to chat" (wa.me) che la pattuglia apre dal proprio telefono per inviarlo.
 */
@Service
public class WhatsappMessageService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_ORA = DateTimeFormatter.ofPattern("HH:mm");

    private final WhatsappProperties properties;

    public WhatsappMessageService(WhatsappProperties properties) {
        this.properties = properties;
    }

    /**
     * Porta il numero inserito dall'amministratore in formato internazionale di sole cifre
     * (quello richiesto da wa.me). Accetta spazi, punti, trattini, "+39", "0039" e i cellulari
     * italiani scritti senza prefisso. Vuoto = nessun telefono (null).
     */
    public String normalizzaTelefono(String grezzo) {
        if (grezzo == null || grezzo.isBlank()) {
            return null;
        }
        String cifre = grezzo.replaceAll("[\\s.\\-()/]", "");
        if (cifre.startsWith("+")) {
            cifre = cifre.substring(1);
        } else if (cifre.startsWith("00")) {
            cifre = cifre.substring(2);
        } else if (cifre.startsWith("3") && (cifre.length() == 9 || cifre.length() == 10)) {
            cifre = properties.getPrefissoDefault() + cifre; // cellulare senza prefisso internazionale
        }
        if (!cifre.matches("\\d{8,15}") || cifre.startsWith("0")) {
            throw new IllegalArgumentException(
                    "Numero di telefono non valido: inserisci un cellulare, con prefisso internazionale se non italiano (es. +39 333 1234567).");
        }
        return cifre;
    }

    /** Testo del messaggio di avvenuto check: il template configurato con i segnaposto sostituiti. */
    public String messaggioCheck(String nomeObiettivo, LocalDateTime oraCheck) {
        return compila(properties.getTemplateCheck(), Map.of(
                "azienda", properties.getNomeAzienda(),
                "obiettivo", nomeObiettivo,
                "data", oraCheck.format(FORMATO_DATA),
                "ora", oraCheck.format(FORMATO_ORA)));
    }

    /** Link wa.me che apre la chat con il numero indicato e il messaggio già compilato. */
    public String linkCheck(String telefono, String nomeObiettivo, LocalDateTime oraCheck) {
        return linkChat(telefono, messaggioCheck(nomeObiettivo, oraCheck));
    }

    String linkChat(String telefono, String messaggio) {
        return "https://wa.me/" + telefono
                + "?text=" + URLEncoder.encode(messaggio, StandardCharsets.UTF_8).replace("+", "%20");
    }

    /** Sostituisce i segnaposto {nome}; quelli sconosciuti restano nel testo così l'errore di configurazione è visibile. */
    String compila(String template, Map<String, String> valori) {
        String testo = template;
        for (Map.Entry<String, String> valore : valori.entrySet()) {
            testo = testo.replace("{" + valore.getKey() + "}", valore.getValue());
        }
        return testo;
    }
}
