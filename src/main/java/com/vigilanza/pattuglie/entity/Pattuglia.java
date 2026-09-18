package com.vigilanza.pattuglie.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pattuglia")
public class Pattuglia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_carburante", nullable = false)
    private TipoCarburante tipoCarburante = TipoCarburante.BENZINA;

    @Column(nullable = false)
    private boolean attiva = true;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    @OneToMany(mappedBy = "pattuglia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Obiettivo> obiettivi = new ArrayList<>();

    @ManyToMany(mappedBy = "pattuglieAbilitate")
    private Set<Utente> utentiAbilitati = new HashSet<>();

    public Pattuglia() {
    }

    public Pattuglia(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // Getters e setters

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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public TipoCarburante getTipoCarburante() {
        return tipoCarburante;
    }

    public void setTipoCarburante(TipoCarburante tipoCarburante) {
        this.tipoCarburante = tipoCarburante;
    }

    public boolean isAttiva() {
        return attiva;
    }

    public void setAttiva(boolean attiva) {
        this.attiva = attiva;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public List<Obiettivo> getObiettivi() {
        return obiettivi;
    }

    public void setObiettivi(List<Obiettivo> obiettivi) {
        this.obiettivi = obiettivi;
    }

    public Set<Utente> getUtentiAbilitati() {
        return utentiAbilitati;
    }

    public void setUtentiAbilitati(Set<Utente> utentiAbilitati) {
        this.utentiAbilitati = utentiAbilitati;
    }
}
