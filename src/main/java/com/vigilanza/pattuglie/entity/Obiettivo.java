package com.vigilanza.pattuglie.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "obiettivo")
public class Obiettivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattuglia_id", nullable = false)
    private Pattuglia pattuglia;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 255)
    private String indirizzo;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitudine;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitudine;

    @Column(name = "ordine_visita")
    private Integer ordineVisita; // popolato dal servizio di ottimizzazione rotta

    @Column(nullable = false)
    private boolean attivo = true;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    public Obiettivo() {
    }

    public Obiettivo(Pattuglia pattuglia, String nome, String indirizzo,
                      BigDecimal latitudine, BigDecimal longitudine) {
        this.pattuglia = pattuglia;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pattuglia getPattuglia() {
        return pattuglia;
    }

    public void setPattuglia(Pattuglia pattuglia) {
        this.pattuglia = pattuglia;
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

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
}
