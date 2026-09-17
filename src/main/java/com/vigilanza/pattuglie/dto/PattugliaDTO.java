package com.vigilanza.pattuglie.dto;

import java.util.List;

public class PattugliaDTO {

    private Long id;
    private String nome;
    private String descrizione;
    private String veicoloTarga;
    private boolean attiva;
    private List<ObiettivoDTO> obiettivi;
    private java.math.BigDecimal consumoMedioL100Km;
    private com.vigilanza.pattuglie.entity.TipoCarburante tipoCarburante;

    public PattugliaDTO() {
    }

    public PattugliaDTO(Long id, String nome, String descrizione, String veicoloTarga,
                         boolean attiva, List<ObiettivoDTO> obiettivi) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.veicoloTarga = veicoloTarga;
        this.attiva = attiva;
        this.obiettivi = obiettivi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getVeicoloTarga() {
        return veicoloTarga;
    }

    public void setVeicoloTarga(String veicoloTarga) {
        this.veicoloTarga = veicoloTarga;
    }

    public boolean isAttiva() {
        return attiva;
    }

    public void setAttiva(boolean attiva) {
        this.attiva = attiva;
    }

    public List<ObiettivoDTO> getObiettivi() {
        return obiettivi;
    }

    public void setObiettivi(List<ObiettivoDTO> obiettivi) {
        this.obiettivi = obiettivi;
    }

    public java.math.BigDecimal getConsumoMedioL100Km() {
        return consumoMedioL100Km;
    }

    public void setConsumoMedioL100Km(java.math.BigDecimal consumoMedioL100Km) {
        this.consumoMedioL100Km = consumoMedioL100Km;
    }

    public com.vigilanza.pattuglie.entity.TipoCarburante getTipoCarburante() {
        return tipoCarburante;
    }

    public void setTipoCarburante(com.vigilanza.pattuglie.entity.TipoCarburante tipoCarburante) {
        this.tipoCarburante = tipoCarburante;
    }
}
