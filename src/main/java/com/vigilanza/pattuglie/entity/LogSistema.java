package com.vigilanza.pattuglie.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_sistema")
public class LogSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento; // es. LOGIN, LOGOUT, FLAG_OBIETTIVO, MODIFICA_ADMIN

    @Column(length = 500)
    private String descrizione;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra = LocalDateTime.now();

    @Column(name = "indirizzo_ip", length = 45)
    private String indirizzoIp;

    public LogSistema() {
    }

    public LogSistema(Utente utente, String tipoEvento, String descrizione, String indirizzoIp) {
        this.utente = utente;
        this.tipoEvento = tipoEvento;
        this.descrizione = descrizione;
        this.indirizzoIp = indirizzoIp;
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public String getIndirizzoIp() {
        return indirizzoIp;
    }

    public void setIndirizzoIp(String indirizzoIp) {
        this.indirizzoIp = indirizzoIp;
    }
}
