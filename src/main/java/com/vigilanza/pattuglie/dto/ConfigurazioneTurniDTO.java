package com.vigilanza.pattuglie.dto;

import java.time.LocalTime;

/** Orari dei tre turni standard (Mattina, Pomeriggio, Notte), usati come proposta rapida per le fasce orarie. */
public class ConfigurazioneTurniDTO {

    private LocalTime mattinaInizio;
    private LocalTime mattinaFine;
    private LocalTime pomeriggioInizio;
    private LocalTime pomeriggioFine;
    /** Di norma successiva a mezzanotte: notteFine è l'ora del giorno DOPO notteInizio. */
    private LocalTime notteInizio;
    private LocalTime notteFine;

    public ConfigurazioneTurniDTO() {
    }

    public ConfigurazioneTurniDTO(LocalTime mattinaInizio, LocalTime mattinaFine, LocalTime pomeriggioInizio,
                                   LocalTime pomeriggioFine, LocalTime notteInizio, LocalTime notteFine) {
        this.mattinaInizio = mattinaInizio;
        this.mattinaFine = mattinaFine;
        this.pomeriggioInizio = pomeriggioInizio;
        this.pomeriggioFine = pomeriggioFine;
        this.notteInizio = notteInizio;
        this.notteFine = notteFine;
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
