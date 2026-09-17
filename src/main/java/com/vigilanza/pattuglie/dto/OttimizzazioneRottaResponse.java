package com.vigilanza.pattuglie.dto;

import java.math.BigDecimal;
import java.util.List;

public class OttimizzazioneRottaResponse {

    private List<ObiettivoDTO> obiettivi;

    private double distanzaOriginaleKm;   // percorrendo gli obiettivi nell'ordine attuale
    private double distanzaOttimizzataKm; // percorrendo gli obiettivi nell'ordine ottimizzato
    private double risparmioKm;
    private double risparmioPercentuale;

    private BigDecimal litriStimatiOriginali;
    private BigDecimal litriStimatiOttimizzati;
    private BigDecimal risparmioLitri;
    private BigDecimal risparmioCosto;   // in valuta, secondo prezzo carburante configurato
    private BigDecimal prezzoCarburanteAlLitro;

    public OttimizzazioneRottaResponse() {
    }

    public List<ObiettivoDTO> getObiettivi() {
        return obiettivi;
    }

    public void setObiettivi(List<ObiettivoDTO> obiettivi) {
        this.obiettivi = obiettivi;
    }

    public double getDistanzaOriginaleKm() {
        return distanzaOriginaleKm;
    }

    public void setDistanzaOriginaleKm(double distanzaOriginaleKm) {
        this.distanzaOriginaleKm = distanzaOriginaleKm;
    }

    public double getDistanzaOttimizzataKm() {
        return distanzaOttimizzataKm;
    }

    public void setDistanzaOttimizzataKm(double distanzaOttimizzataKm) {
        this.distanzaOttimizzataKm = distanzaOttimizzataKm;
    }

    public double getRisparmioKm() {
        return risparmioKm;
    }

    public void setRisparmioKm(double risparmioKm) {
        this.risparmioKm = risparmioKm;
    }

    public double getRisparmioPercentuale() {
        return risparmioPercentuale;
    }

    public void setRisparmioPercentuale(double risparmioPercentuale) {
        this.risparmioPercentuale = risparmioPercentuale;
    }

    public BigDecimal getLitriStimatiOriginali() {
        return litriStimatiOriginali;
    }

    public void setLitriStimatiOriginali(BigDecimal litriStimatiOriginali) {
        this.litriStimatiOriginali = litriStimatiOriginali;
    }

    public BigDecimal getLitriStimatiOttimizzati() {
        return litriStimatiOttimizzati;
    }

    public void setLitriStimatiOttimizzati(BigDecimal litriStimatiOttimizzati) {
        this.litriStimatiOttimizzati = litriStimatiOttimizzati;
    }

    public BigDecimal getRisparmioLitri() {
        return risparmioLitri;
    }

    public void setRisparmioLitri(BigDecimal risparmioLitri) {
        this.risparmioLitri = risparmioLitri;
    }

    public BigDecimal getRisparmioCosto() {
        return risparmioCosto;
    }

    public void setRisparmioCosto(BigDecimal risparmioCosto) {
        this.risparmioCosto = risparmioCosto;
    }

    public BigDecimal getPrezzoCarburanteAlLitro() {
        return prezzoCarburanteAlLitro;
    }

    public void setPrezzoCarburanteAlLitro(BigDecimal prezzoCarburanteAlLitro) {
        this.prezzoCarburanteAlLitro = prezzoCarburanteAlLitro;
    }
}
