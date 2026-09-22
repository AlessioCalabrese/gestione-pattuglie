package com.vigilanza.pattuglie.dto;

import java.util.List;

/** Gruppo di accorpamento di pattuglie: le pattuglie membro vedono e possono flaggare gli obiettivi le une delle altre. */
public class GruppoPattuglieDTO {

    private final Long id;
    private final String nome;
    private final List<MembroGruppoDTO> membri;

    public GruppoPattuglieDTO(Long id, String nome, List<MembroGruppoDTO> membri) {
        this.id = id;
        this.nome = nome;
        this.membri = membri;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<MembroGruppoDTO> getMembri() {
        return membri;
    }
}
