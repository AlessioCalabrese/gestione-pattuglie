package com.vigilanza.pattuglie.dto;

import com.vigilanza.pattuglie.entity.GiornoSettimana;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

public class ObiettivoDTO {

    private Long id;
    private Long pattugliaId;
    private String nome;

    private String via;
    private String numeroCivico;
    private String comune;
    /** Indirizzo completo, ricostruito lato server per comodità di visualizzazione. */
    private String indirizzo;

    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private Integer ordineVisita;
    private boolean attivo;
    private boolean priorita;

    private Set<GiornoSettimana> giorniAttivi;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private int ripetizioniGiornaliere;

    // Stato calcolato lato servizio
    private Boolean flaggatoOggi;
    private String ultimoFlagDataOra;
    private int numeroFlagOggi;
    private boolean completatoOggi; // numeroFlagOggi >= ripetizioniGiornaliere
    private boolean inServizioOra;  // rispetta la pianificazione (giorno + fascia oraria) in questo momento

    public ObiettivoDTO() {
    }

    public ObiettivoDTO(Long id, Long pattugliaId, String nome, String via, String numeroCivico, String comune,
                         BigDecimal latitudine, BigDecimal longitudine,
                         Integer ordineVisita, boolean attivo) {
        this.id = id;
        this.pattugliaId = pattugliaId;
        this.nome = nome;
        this.via = via;
        this.numeroCivico = numeroCivico;
        this.comune = comune;
        this.indirizzo = via + " " + numeroCivico + ", " + comune;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.ordineVisita = ordineVisita;
        this.attivo = attivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPattugliaId() {
        return pattugliaId;
    }

    public void setPattugliaId(Long pattugliaId) {
        this.pattugliaId = pattugliaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getNumeroCivico() {
        return numeroCivico;
    }

    public void setNumeroCivico(String numeroCivico) {
        this.numeroCivico = numeroCivico;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
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

    public Integer getOrdineVisita() {
        return ordineVisita;
    }

    public void setOrdineVisita(Integer ordineVisita) {
        this.ordineVisita = ordineVisita;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    public boolean isPriorita() {
        return priorita;
    }

    public void setPriorita(boolean priorita) {
        this.priorita = priorita;
    }

    public Set<GiornoSettimana> getGiorniAttivi() {
        return giorniAttivi;
    }

    public void setGiorniAttivi(Set<GiornoSettimana> giorniAttivi) {
        this.giorniAttivi = giorniAttivi;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public void setOraFine(LocalTime oraFine) {
        this.oraFine = oraFine;
    }

    public int getRipetizioniGiornaliere() {
        return ripetizioniGiornaliere;
    }

    public void setRipetizioniGiornaliere(int ripetizioniGiornaliere) {
        this.ripetizioniGiornaliere = ripetizioniGiornaliere;
    }

    public Boolean getFlaggatoOggi() {
        return flaggatoOggi;
    }

    public void setFlaggatoOggi(Boolean flaggatoOggi) {
        this.flaggatoOggi = flaggatoOggi;
    }

    public String getUltimoFlagDataOra() {
        return ultimoFlagDataOra;
    }

    public void setUltimoFlagDataOra(String ultimoFlagDataOra) {
        this.ultimoFlagDataOra = ultimoFlagDataOra;
    }

    public int getNumeroFlagOggi() {
        return numeroFlagOggi;
    }

    public void setNumeroFlagOggi(int numeroFlagOggi) {
        this.numeroFlagOggi = numeroFlagOggi;
    }

    public boolean isCompletatoOggi() {
        return completatoOggi;
    }

    public void setCompletatoOggi(boolean completatoOggi) {
        this.completatoOggi = completatoOggi;
    }

    public boolean isInServizioOra() {
        return inServizioOra;
    }

    public void setInServizioOra(boolean inServizioOra) {
        this.inServizioOra = inServizioOra;
    }
}
