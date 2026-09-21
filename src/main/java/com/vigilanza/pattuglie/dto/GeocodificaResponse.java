package com.vigilanza.pattuglie.dto;

import java.math.BigDecimal;

public class GeocodificaResponse {

    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private String indirizzoNormalizzato;
    /** Nome della strada del risultato (es. "Via Giuseppe Garibaldi"); null se il luogo trovato non è una strada. */
    private String strada;

    public GeocodificaResponse() {
    }

    public GeocodificaResponse(BigDecimal latitudine, BigDecimal longitudine, String indirizzoNormalizzato) {
        this(latitudine, longitudine, indirizzoNormalizzato, null);
    }

    public GeocodificaResponse(BigDecimal latitudine, BigDecimal longitudine, String indirizzoNormalizzato, String strada) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.indirizzoNormalizzato = indirizzoNormalizzato;
        this.strada = strada;
    }

    public String getStrada() {
        return strada;
    }

    public void setStrada(String strada) {
        this.strada = strada;
    }

    public BigDecimal getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(BigDecimal latitudine) {
        this.latitudine = latitudine;
    }

    public BigDecimal getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(BigDecimal longitudine) {
        this.longitudine = longitudine;
    }

    public String getIndirizzoNormalizzato() {
        return indirizzoNormalizzato;
    }

    public void setIndirizzoNormalizzato(String indirizzoNormalizzato) {
        this.indirizzoNormalizzato = indirizzoNormalizzato;
    }
}
