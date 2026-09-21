package com.vigilanza.pattuglie.dto;

import com.vigilanza.pattuglie.entity.TipoCarburante;

import java.util.List;

/** Pattuglia come appare nella ricerca amministrativa, senza caricare tutti i suoi obiettivi. */
public class PattugliaRicercaDTO {

    private Long id;
    private String nome;
    private String descrizione;
    private boolean attiva;
    private TipoCarburante tipoCarburante;
    /** Nomi degli obiettivi che corrispondono al testo cercato (vuoto se non si sta cercando o se la corrispondenza è sul nome/descrizione della pattuglia). */
    private List<String> obiettiviCorrispondenti = List.of();

    public PattugliaRicercaDTO() {
    }

    public PattugliaRicercaDTO(Long id, String nome, String descrizione, boolean attiva,
                                TipoCarburante tipoCarburante, List<String> obiettiviCorrispondenti) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.attiva = attiva;
        this.tipoCarburante = tipoCarburante;
        this.obiettiviCorrispondenti = obiettiviCorrispondenti;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public boolean isAttiva() {
        return attiva;
    }

    public TipoCarburante getTipoCarburante() {
        return tipoCarburante;
    }

    public List<String> getObiettiviCorrispondenti() {
        return obiettiviCorrispondenti;
    }
}
