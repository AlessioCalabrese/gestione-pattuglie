package com.vigilanza.pattuglie.dto;

import java.util.List;

/** Una tratta del percorso consigliato, apribile in Google Maps per avviare la navigazione. */
public class TrattaNavigatoreDTO {

    private final int numero;
    /** Nomi degli obiettivi della tratta, nell'ordine di visita. */
    private final List<String> tappe;
    private final String url;

    public TrattaNavigatoreDTO(int numero, List<String> tappe, String url) {
        this.numero = numero;
        this.tappe = tappe;
        this.url = url;
    }

    public int getNumero() {
        return numero;
    }

    public List<String> getTappe() {
        return tappe;
    }

    public String getUrl() {
        return url;
    }
}
