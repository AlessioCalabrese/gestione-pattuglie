package com.vigilanza.pattuglie.dto;

import java.math.BigDecimal;

/**
 * Correzione proposta per un obiettivo il cui indirizzo non ha una corrispondenza esatta: l'utente
 * la conferma (la via e le coordinate vengono aggiornate) oppure la scarta (l'obiettivo resta com'è).
 */
public class PropostaIndirizzoDTO {

    private final Long obiettivoId;
    private final String nome;
    private final String viaAttuale;
    private final String comune;
    /** Via proposta al posto di quella inserita; null se il luogo trovato non è una strada (si aggiornano solo le coordinate). */
    private final String viaProposta;
    /** Indirizzo completo restituito dal servizio di geocodifica, come riferimento per l'utente. */
    private final String indirizzoTrovato;
    private final BigDecimal latitudine;
    private final BigDecimal longitudine;

    public PropostaIndirizzoDTO(Long obiettivoId, String nome, String viaAttuale, String comune, String viaProposta,
                                 String indirizzoTrovato, BigDecimal latitudine, BigDecimal longitudine) {
        this.obiettivoId = obiettivoId;
        this.nome = nome;
        this.viaAttuale = viaAttuale;
        this.comune = comune;
        this.viaProposta = viaProposta;
        this.indirizzoTrovato = indirizzoTrovato;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    public Long getObiettivoId() {
        return obiettivoId;
    }

    public String getNome() {
        return nome;
    }

    public String getViaAttuale() {
        return viaAttuale;
    }

    public String getComune() {
        return comune;
    }

    public String getViaProposta() {
        return viaProposta;
    }

    public String getIndirizzoTrovato() {
        return indirizzoTrovato;
    }

    public BigDecimal getLatitudine() {
        return latitudine;
    }

    public BigDecimal getLongitudine() {
        return longitudine;
    }
}
