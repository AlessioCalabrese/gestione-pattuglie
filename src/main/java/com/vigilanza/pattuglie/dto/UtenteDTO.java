package com.vigilanza.pattuglie.dto;

import com.vigilanza.pattuglie.entity.RuoloUtente;

public class UtenteDTO {

    private Long id;
    private String username;
    private String nome;
    private String cognome;
    private RuoloUtente ruolo;
    private boolean abilitato;
    private String nfcTagId;

    public UtenteDTO() {
    }

    public UtenteDTO(Long id, String username, String nome, String cognome,
                      RuoloUtente ruolo, boolean abilitato) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
        this.abilitato = abilitato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public RuoloUtente getRuolo() {
        return ruolo;
    }

    public void setRuolo(RuoloUtente ruolo) {
        this.ruolo = ruolo;
    }

    public boolean isAbilitato() {
        return abilitato;
    }

    public void setAbilitato(boolean abilitato) {
        this.abilitato = abilitato;
    }

    public String getNfcTagId() {
        return nfcTagId;
    }

    public void setNfcTagId(String nfcTagId) {
        this.nfcTagId = nfcTagId;
    }
}
