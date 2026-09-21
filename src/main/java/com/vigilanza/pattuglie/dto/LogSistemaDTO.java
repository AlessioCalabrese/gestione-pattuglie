package com.vigilanza.pattuglie.dto;

import com.vigilanza.pattuglie.entity.LogSistema;

import java.time.LocalDateTime;

/** Evento del log di sistema, come mostrato nella schermata di amministrazione. */
public class LogSistemaDTO {

    private final Long id;
    private final LocalDateTime dataOra;
    /** Username di chi ha generato l'evento; null per eventi di sistema o utente eliminato. */
    private final String username;
    /** "Nome Cognome" dell'utente; null come sopra. */
    private final String nomeUtente;
    private final String tipoEvento;
    private final String descrizione;
    private final String indirizzoIp;

    public LogSistemaDTO(LogSistema log) {
        this.id = log.getId();
        this.dataOra = log.getDataOra();
        this.username = log.getUtente() == null ? null : log.getUtente().getUsername();
        this.nomeUtente = log.getUtente() == null ? null
                : (log.getUtente().getNome() + " " + log.getUtente().getCognome()).trim();
        this.tipoEvento = log.getTipoEvento();
        this.descrizione = log.getDescrizione();
        this.indirizzoIp = log.getIndirizzoIp();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public String getUsername() {
        return username;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getIndirizzoIp() {
        return indirizzoIp;
    }
}
