package com.vigilanza.pattuglie.dto;

import com.vigilanza.pattuglie.entity.GiornoSettimana;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

public class NuovoObiettivoRequest {

    private String nome;
    private String via;
    private String numeroCivico;
    private String comune;
    private BigDecimal latitudine;
    private BigDecimal longitudine;
    private boolean priorita;

    /** Giorni in cui l'obiettivo è in servizio. Vuoto/null = tutti i giorni. */
    private Set<GiornoSettimana> giorniAttivi;

    /** Fascia oraria di servizio, formato "HH:mm". Null = nessun vincolo. */
    private LocalTime oraInizio;
    private LocalTime oraFine;

    /** Quante volte va flaggato nella giornata/fascia oraria. Default 1. */
    private Integer ripetizioniGiornaliere;

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

    public Integer getRipetizioniGiornaliere() {
        return ripetizioniGiornaliere;
    }

    public void setRipetizioniGiornaliere(Integer ripetizioniGiornaliere) {
        this.ripetizioniGiornaliere = ripetizioniGiornaliere;
    }
}
