package com.vigilanza.pattuglie.dto;

/** Posizione stimata dalla rete quando il GPS non è disponibile. */
public class PosizioneDTO {

    /** Da dove viene la stima: l'IP pubblico del client (RETE) o la sede di riferimento configurata (SEDE). */
    public enum Fonte { RETE, SEDE }

    private final double latitudine;
    private final double longitudine;
    /** Errore massimo stimato, in metri: la posizione da IP è approssimativa (livello di città). */
    private final int precisioneMetri;
    private final Fonte fonte;
    /** Località individuata (es. "Avellino, Campania"), per mostrarla all'utente; può essere null. */
    private final String localita;

    public PosizioneDTO(double latitudine, double longitudine, int precisioneMetri, Fonte fonte, String localita) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.precisioneMetri = precisioneMetri;
        this.fonte = fonte;
        this.localita = localita;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public int getPrecisioneMetri() {
        return precisioneMetri;
    }

    public Fonte getFonte() {
        return fonte;
    }

    public String getLocalita() {
        return localita;
    }
}
