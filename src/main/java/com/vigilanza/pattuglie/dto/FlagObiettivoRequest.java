package com.vigilanza.pattuglie.dto;

import java.math.BigDecimal;

/**
 * Payload inviato dal frontend Angular al momento del flag,
 * con le coordinate rilevate dal browser/tablet (Geolocation API).
 */
public class FlagObiettivoRequest {

    private Long obiettivoId;
    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private BigDecimal precisioneMetri; // accuracy restituita dal GPS
    private String note;

    public FlagObiettivoRequest() {
    }

    public Long getObiettivoId() {
        return obiettivoId;
    }

    public void setObiettivoId(Long obiettivoId) {
        this.obiettivoId = obiettivoId;
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

    public BigDecimal getPrecisioneMetri() {
        return precisioneMetri;
    }

    public void setPrecisioneMetri(BigDecimal precisioneMetri) {
        this.precisioneMetri = precisioneMetri;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
