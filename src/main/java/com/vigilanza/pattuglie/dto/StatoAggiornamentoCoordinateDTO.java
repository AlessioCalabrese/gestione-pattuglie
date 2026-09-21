package com.vigilanza.pattuglie.dto;

import java.util.List;

/** Avanzamento (o esito finale) dell'aggiornamento massivo delle coordinate degli obiettivi. */
public class StatoAggiornamentoCoordinateDTO {

    public enum Stato { MAI_ESEGUITO, IN_CORSO, COMPLETATO, ERRORE }

    private final Stato stato;
    private final int totale;
    private final int elaborati;
    private final int aggiornati;
    /** Obiettivi attivi che avevano già le coordinate e sono quindi stati saltati. */
    private final int giaConCoordinate;
    private final int errori;
    /** Obiettivi il cui indirizzo non è stato trovato (restano senza coordinate). */
    private final List<String> nonTrovati;
    /** Correzioni di indirizzo in attesa di conferma da parte dell'utente. */
    private final List<PropostaIndirizzoDTO> proposte;
    private final String messaggio;

    public StatoAggiornamentoCoordinateDTO(Stato stato, int totale, int elaborati, int aggiornati, int giaConCoordinate,
                                            int errori, List<String> nonTrovati, List<PropostaIndirizzoDTO> proposte,
                                            String messaggio) {
        this.stato = stato;
        this.totale = totale;
        this.elaborati = elaborati;
        this.aggiornati = aggiornati;
        this.giaConCoordinate = giaConCoordinate;
        this.errori = errori;
        this.nonTrovati = nonTrovati;
        this.proposte = proposte;
        this.messaggio = messaggio;
    }

    public Stato getStato() {
        return stato;
    }

    public int getTotale() {
        return totale;
    }

    public int getElaborati() {
        return elaborati;
    }

    public int getAggiornati() {
        return aggiornati;
    }

    public int getGiaConCoordinate() {
        return giaConCoordinate;
    }

    public int getErrori() {
        return errori;
    }

    public List<String> getNonTrovati() {
        return nonTrovati;
    }

    public List<PropostaIndirizzoDTO> getProposte() {
        return proposte;
    }

    public String getMessaggio() {
        return messaggio;
    }
}
