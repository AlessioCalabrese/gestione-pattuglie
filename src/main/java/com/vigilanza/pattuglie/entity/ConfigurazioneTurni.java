package com.vigilanza.pattuglie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalTime;

/**
 * Orari di default dei tre turni standard (Mattina, Pomeriggio, Notte), usati come proposta rapida
 * quando l'amministratore configura le fasce orarie di un obiettivo. Riga singola (id fisso = 1),
 * modificabile dall'amministratore. La Notte, di norma, attraversa la mezzanotte (es. 22:00–06:00 del
 * giorno dopo): vedi {@link FasciaOraria#isNotturna()} per la stessa logica usata sulle fasce degli obiettivi.
 */
@Entity
@Table(name = "configurazione_turni")
public class ConfigurazioneTurni {

    public static final Long ID_UNICO = 1L;

    @Id
    private Long id = ID_UNICO;

    @Column(name = "mattina_inizio", nullable = false)
    private LocalTime mattinaInizio = LocalTime.of(6, 0);

    @Column(name = "mattina_fine", nullable = false)
    private LocalTime mattinaFine = LocalTime.of(14, 0);

    @Column(name = "pomeriggio_inizio", nullable = false)
    private LocalTime pomeriggioInizio = LocalTime.of(14, 0);

    @Column(name = "pomeriggio_fine", nullable = false)
    private LocalTime pomeriggioFine = LocalTime.of(22, 0);

    @Column(name = "notte_inizio", nullable = false)
    private LocalTime notteInizio = LocalTime.of(22, 0);

    /** Ora di fine della notte, del giorno successivo a notteInizio. */
    @Column(name = "notte_fine", nullable = false)
    private LocalTime notteFine = LocalTime.of(6, 0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getMattinaInizio() {
        return mattinaInizio;
    }

    public void setMattinaInizio(LocalTime mattinaInizio) {
        this.mattinaInizio = mattinaInizio;
    }

    public LocalTime getMattinaFine() {
        return mattinaFine;
    }

    public void setMattinaFine(LocalTime mattinaFine) {
        this.mattinaFine = mattinaFine;
    }

    public LocalTime getPomeriggioInizio() {
        return pomeriggioInizio;
    }

    public void setPomeriggioInizio(LocalTime pomeriggioInizio) {
        this.pomeriggioInizio = pomeriggioInizio;
    }

    public LocalTime getPomeriggioFine() {
        return pomeriggioFine;
    }

    public void setPomeriggioFine(LocalTime pomeriggioFine) {
        this.pomeriggioFine = pomeriggioFine;
    }

    public LocalTime getNotteInizio() {
        return notteInizio;
    }

    public void setNotteInizio(LocalTime notteInizio) {
        this.notteInizio = notteInizio;
    }

    public LocalTime getNotteFine() {
        return notteFine;
    }

    public void setNotteFine(LocalTime notteFine) {
        this.notteFine = notteFine;
    }
}
