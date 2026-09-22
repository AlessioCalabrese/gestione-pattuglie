package com.vigilanza.pattuglie.dto;

import com.vigilanza.pattuglie.entity.TipoObiettivo;

import java.math.BigDecimal;
import java.util.List;

public class NuovoObiettivoRequest {

    private String nome;
    private String via;
    private String comune;
    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private boolean priorita;
    private TipoObiettivo tipoObiettivo;

    /**
     * Fasce orarie di servizio: ogni voce è legata a un giorno della settimana e può avere le proprie
     * ripetizioni richieste. Un giorno può comparire più volte (es. mattina e sera). Obbligatorio: un
     * obiettivo senza nessuna fascia non sarebbe mai visibile alle pattuglie.
     */
    private List<FasciaOrariaDTO> fasceOrarie;

    /** Cellulare di riferimento per l'avviso WhatsApp (opzionale). Accetta anche spazi e prefisso +39. */
    private String telefonoRiferimento;

    public NuovoObiettivoRequest() {
    }

    public String getTelefonoRiferimento() {
        return telefonoRiferimento;
    }

    public void setTelefonoRiferimento(String telefonoRiferimento) {
        this.telefonoRiferimento = telefonoRiferimento;
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

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
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

    public boolean isPriorita() {
        return priorita;
    }

    public void setPriorita(boolean priorita) {
        this.priorita = priorita;
    }

    public TipoObiettivo getTipoObiettivo() {
        return tipoObiettivo;
    }

    public void setTipoObiettivo(TipoObiettivo tipoObiettivo) {
        this.tipoObiettivo = tipoObiettivo;
    }

    public List<FasciaOrariaDTO> getFasceOrarie() {
        return fasceOrarie;
    }

    public void setFasceOrarie(List<FasciaOrariaDTO> fasceOrarie) {
        this.fasceOrarie = fasceOrarie;
    }
}
