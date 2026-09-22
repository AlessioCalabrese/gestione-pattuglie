package com.vigilanza.pattuglie.dto;

import java.math.BigDecimal;
import java.util.List;

public class OttimizzazioneRottaResponse {

    private List<ObiettivoDTO> obiettivi;

    private int obiettiviInServizioOggi; // quanti degli obiettivi restituiti fanno parte del percorso odierno

    private double distanzaOriginaleKm;   // percorrendo gli obiettivi nell'ordine attuale
    private double distanzaOttimizzataKm; // percorrendo gli obiettivi nell'ordine ottimizzato
    private double risparmioKm;
    private double risparmioPercentuale;

    private BigDecimal litriStimatiOriginali;
    private BigDecimal litriStimatiOttimizzati;
    private BigDecimal risparmioLitri;
    private BigDecimal risparmioCosto;   // in valuta, secondo prezzo carburante configurato
    private BigDecimal prezzoCarburanteAlLitro;

    /** Link Google Maps del percorso consigliato: uno per ogni tratta di al massimo 10 tappe. */
    private List<TrattaNavigatoreDTO> tratteNavigatore = List.of();

    public OttimizzazioneRottaResponse() {
    }

    public List<TrattaNavigatoreDTO> getTratteNavigatore() {
        return tratteNavigatore;
    }

    public void setTratteNavigatore(List<TrattaNavigatoreDTO> tratteNavigatore) {
        this.tratteNavigatore = tratteNavigatore;
    }

    public List<ObiettivoDTO> getObiettivi() {
        return obiettivi;
    }

    public void setObiettivi(List<ObiettivoDTO> obiettivi) {
        this.obiettivi = obiettivi;
    }

    public int getObiettiviInServizioOggi() {
        return obiettiviInServizioOggi;
    }

    public void setObiettiviInServizioOggi(int obiettiviInServizioOggi) {
        this.obiettiviInServizioOggi = obiettiviInServizioOggi;
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
