package com.vigilanza.pattuglie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurazione dei messaggi WhatsApp (prefisso "app.whatsapp" in application.properties).
 * I valori di default qui sotto valgono se le proprietà non sono impostate.
 */
@Component
@ConfigurationProperties(prefix = "app.whatsapp")
public class WhatsappProperties {

    /**
     * Testo del messaggio inviato all'obiettivo dopo il check. Segnaposto disponibili:
     * {azienda}, {obiettivo}, {data} (dd/MM/yyyy), {ora} (HH:mm).
     */
    private String templateCheck = "Gentile cliente, la informiamo che la pattuglia {azienda} ha effettuato "
            + "il controllo presso {obiettivo} il {data} alle ore {ora}. "
            + "La ringraziamo per aver scelto il servizio {azienda}.";

    /** Valore del segnaposto {azienda}. */
    private String nomeAzienda = "COSMOPOL";

    /** Prefisso telefonico applicato ai cellulari inseriti senza prefisso internazionale (solo cifre, senza +). */
    private String prefissoDefault = "39";

    public String getTemplateCheck() {
        return templateCheck;
    }

    public void setTemplateCheck(String templateCheck) {
        this.templateCheck = templateCheck;
    }

    public String getNomeAzienda() {
        return nomeAzienda;
    }

    public void setNomeAzienda(String nomeAzienda) {
        this.nomeAzienda = nomeAzienda;
    }

    public String getPrefissoDefault() {
        return prefissoDefault;
    }

    public void setPrefissoDefault(String prefissoDefault) {
        this.prefissoDefault = prefissoDefault;
    }
}
