package com.vigilanza.pattuglie.dto;

import com.vigilanza.pattuglie.entity.GiornoSettimana;

import java.time.LocalTime;

/**
 * Una fascia oraria di servizio configurata su un obiettivo: usata sia per crearle/modificarle
 * (in {@code NuovoObiettivoRequest}) sia per elencare quelle già configurate (in {@code ObiettivoDTO}).
 */
public class FasciaOrariaDTO {

    private GiornoSettimana giorno;
    /** Formato "HH:mm"; null = nessun vincolo dall'inizio/fino alla fine della giornata. */
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private int ripetizioniRichieste = 1;

    public FasciaOrariaDTO() {
    }

    public FasciaOrariaDTO(GiornoSettimana giorno, LocalTime oraInizio, LocalTime oraFine, int ripetizioniRichieste) {
        this.giorno = giorno;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.ripetizioniRichieste = ripetizioniRichieste;
    }

    public GiornoSettimana getGiorno() {
        return giorno;
    }

    public void setGiorno(GiornoSettimana giorno) {
        this.giorno = giorno;
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

    public int getRipetizioniRichieste() {
        return ripetizioniRichieste;
    }

    public void setRipetizioniRichieste(int ripetizioniRichieste) {
        this.ripetizioniRichieste = ripetizioniRichieste;
    }
}
