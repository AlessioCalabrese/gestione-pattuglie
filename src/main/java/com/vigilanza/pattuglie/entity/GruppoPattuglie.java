package com.vigilanza.pattuglie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Accorpamento di due o più pattuglie, configurato dall'amministratore: le pattuglie di uno stesso
 * gruppo vedono e possono flaggare anche gli obiettivi delle altre pattuglie del gruppo (vedi
 * {@link com.vigilanza.pattuglie.service.GruppoPattuglieService}). Una pattuglia appartiene al massimo
 * a un gruppo (vedi {@link Pattuglia#getGruppo()}).
 */
@Entity
@Table(name = "gruppo_pattuglie")
public class GruppoPattuglie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    public GruppoPattuglie() {
    }

    public GruppoPattuglie(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
}
