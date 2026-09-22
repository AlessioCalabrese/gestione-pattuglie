package com.vigilanza.pattuglie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Una fascia oraria di servizio di un obiettivo, per un giorno della settimana. Un obiettivo può averne
 * più di una nello stesso giorno (es. mattina e sera) e fasce diverse in giorni diversi.
 */
@Embeddable
public class FasciaOraria {

    @Enumerated(EnumType.STRING)
    @Column(name = "giorno", nullable = false, length = 15)
    private GiornoSettimana giorno;

    /** Null = nessun vincolo dall'inizio della giornata. */
    @Column(name = "ora_inizio")
    private LocalTime oraInizio;

    /** Null = nessun vincolo fino alla fine della giornata. */
    @Column(name = "ora_fine")
    private LocalTime oraFine;

    @Column(name = "ripetizioni_richieste", nullable = false)
    private int ripetizioniRichieste = 1;

    public FasciaOraria() {
    }

    public FasciaOraria(GiornoSettimana giorno, LocalTime oraInizio, LocalTime oraFine, int ripetizioniRichieste) {
        this.giorno = giorno;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.ripetizioniRichieste = ripetizioniRichieste;
    }

    /**
     * Vero se questa fascia attraversa la mezzanotte (es. 22:00–06:00: inizia un giorno e finisce nel
     * successivo). Richiede che entrambi gli estremi siano indicati: una fascia con un estremo libero
     * ("dall'inizio"/"fino alla fine della giornata") non è mai considerata notturna.
     */
    public boolean isNotturna() {
        return oraInizio != null && oraFine != null && oraFine.isBefore(oraInizio);
    }

    /** Vero se l'ora indicata rientra nella parte di questa fascia che cade nello stesso giorno di inizio. */
    public boolean contiene(LocalTime ora) {
        if (isNotturna()) {
            return !ora.isBefore(oraInizio) || !ora.isAfter(oraFine);
        }
        return (oraInizio == null || !ora.isBefore(oraInizio)) && (oraFine == null || !ora.isAfter(oraFine));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FasciaOraria that)) return false;
        return ripetizioniRichieste == that.ripetizioniRichieste && giorno == that.giorno
                && Objects.equals(oraInizio, that.oraInizio) && Objects.equals(oraFine, that.oraFine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(giorno, oraInizio, oraFine, ripetizioniRichieste);
    }
}
