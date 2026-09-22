package com.vigilanza.pattuglie.dto;

/** Pattuglia membro di un gruppo di accorpamento, come mostrata nella schermata di amministrazione. */
public class MembroGruppoDTO {

    private final Long id;
    private final String nome;
    private final String descrizione;
    private final boolean attiva;

    public MembroGruppoDTO(Long id, String nome, String descrizione, boolean attiva) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.attiva = attiva;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public boolean isAttiva() {
        return attiva;
    }
}
