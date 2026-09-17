package com.vigilanza.pattuglie.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registrazione di un passaggio su un obiettivo. Tabella append-only:
 * ogni flag genera un nuovo record, così lo stesso obiettivo può essere
 * flaggato più volte nell'arco della giornata mantenendo lo storico.
 */
@Entity
@Table(name = "obiettivo_flag")
public class ObiettivoFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obiettivo_id", nullable = false)
    private Obiettivo obiettivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattuglia_id", nullable = false)
    private Pattuglia pattuglia;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra = LocalDateTime.now();

    @Column(name = "latitudine_reg", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitudineRegistrazione;

    @Column(name = "longitudine_reg", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitudineRegistrazione;

    @Column(name = "precisione_metri", precision = 6, scale = 2)
    private BigDecimal precisioneMetri;

    @Column(length = 255)
    private String note;

    public ObiettivoFlag() {
    }

    public ObiettivoFlag(Obiettivo obiettivo, Utente utente, Pattuglia pattuglia,
                          BigDecimal latitudineRegistrazione, BigDecimal longitudineRegistrazione) {
        this.obiettivo = obiettivo;
        this.utente = utente;
        this.pattuglia = pattuglia;
        this.latitudineRegistrazione = latitudineRegistrazione;
        this.longitudineRegistrazione = longitudineRegistrazione;
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Obiettivo getObiettivo() {
        return obiettivo;
    }

    public void setObiettivo(Obiettivo obiettivo) {
        this.obiettivo = obiettivo;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Pattuglia getPattuglia() {
        return pattuglia;
    }

    public void setPattuglia(Pattuglia pattuglia) {
        this.pattuglia = pattuglia;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public BigDecimal getLatitudineRegistrazione() {
        return latitudineRegistrazione;
    }

    public void setLatitudineRegistrazione(BigDecimal latitudineRegistrazione) {
        this.latitudineRegistrazione = latitudineRegistrazione;
    }

    public BigDecimal getLongitudineRegistrazione() {
        return longitudineRegistrazione;
    }

    public void setLongitudineRegistrazione(BigDecimal longitudineRegistrazione) {
        this.longitudineRegistrazione = longitudineRegistrazione;
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
