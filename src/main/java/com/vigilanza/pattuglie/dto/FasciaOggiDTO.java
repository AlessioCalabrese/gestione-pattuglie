package com.vigilanza.pattuglie.dto;

import java.time.LocalTime;

/** Stato di una fascia oraria di oggi, per la pattuglia: quanti flag mancano e se è quella in corso adesso. */
public class FasciaOggiDTO {

    private LocalTime oraInizio;
    private LocalTime oraFine;
    private int ripetizioniRichieste;
    private int numeroFlag;
    private boolean completata; // numeroFlag >= ripetizioniRichieste
    private boolean inCorsoOra; // l'ora attuale rientra in questa fascia
    /** Vero se è la coda di un turno notturno iniziato ieri (es. 22:00–06:00), non un turno che inizia oggi. */
    private boolean continuaDaIeri;

    public FasciaOggiDTO() {
    }

    public FasciaOggiDTO(LocalTime oraInizio, LocalTime oraFine, int ripetizioniRichieste,
                          int numeroFlag, boolean completata, boolean inCorsoOra, boolean continuaDaIeri) {
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.ripetizioniRichieste = ripetizioniRichieste;
        this.numeroFlag = numeroFlag;
        this.completata = completata;
        this.inCorsoOra = inCorsoOra;
        this.continuaDaIeri = continuaDaIeri;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public int getRipetizioniRichieste() {
        return ripetizioniRichieste;
    }

    public int getNumeroFlag() {
        return numeroFlag;
    }

    public boolean isCompletata() {
        return completata;
    }

    public boolean isInCorsoOra() {
        return inCorsoOra;
    }

    public boolean isContinuaDaIeri() {
        return continuaDaIeri;
    }
}
