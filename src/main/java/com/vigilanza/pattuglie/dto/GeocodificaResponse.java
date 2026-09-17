package com.vigilanza.pattuglie.dto;

import java.math.BigDecimal;

public class GeocodificaResponse {

    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private String indirizzoNormalizzato;

    public GeocodificaResponse() {
    }

    public GeocodificaResponse(BigDecimal latitudine, BigDecimal longitudine, String indirizzoNormalizzato) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.indirizzoNormalizzato = indirizzoNormalizzato;
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
