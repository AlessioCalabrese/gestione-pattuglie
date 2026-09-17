package com.vigilanza.pattuglie.dto;

import java.math.BigDecimal;

public class ObiettivoDTO {

    private Long id;
    private Long pattugliaId;
    private String nome;
    private String indirizzo;
    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private Integer ordineVisita;
    private boolean attivo;

    // Stato calcolato lato servizio: ultimo flag effettuato oggi (se presente)
    private Boolean flaggatoOggi;
    private String ultimoFlagDataOra;

    public ObiettivoDTO() {
    }

    public ObiettivoDTO(Long id, Long pattugliaId, String nome, String indirizzo,
                         BigDecimal latitudine, BigDecimal longitudine,
                         Integer ordineVisita, boolean attivo) {
        this.id = id;
        this.pattugliaId = pattugliaId;
        this.nome = nome;
        this.indirizzo = indirizzo;
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
}
