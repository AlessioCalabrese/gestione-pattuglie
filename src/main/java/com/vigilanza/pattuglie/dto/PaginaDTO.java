package com.vigilanza.pattuglie.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/** Una pagina di risultati, con i dati necessari al frontend per la paginazione (pagina numerata da 0). */
public class PaginaDTO<T> {

    private List<T> contenuto;
    private int pagina;
    private int dimensione;
    private long totaleElementi;
    private int totalePagine;

    public PaginaDTO() {
    }

    public static <E, T> PaginaDTO<T> da(Page<E> page, Function<E, T> conversione) {
        PaginaDTO<T> dto = new PaginaDTO<>();
        dto.contenuto = page.getContent().stream().map(conversione).toList();
        dto.pagina = page.getNumber();
        dto.dimensione = page.getSize();
        dto.totaleElementi = page.getTotalElements();
        dto.totalePagine = page.getTotalPages();
        return dto;
    }

    public List<T> getContenuto() {
        return contenuto;
    }

    public int getPagina() {
        return pagina;
    }

    public int getDimensione() {
        return dimensione;
    }

    public long getTotaleElementi() {
        return totaleElementi;
    }

    public int getTotalePagine() {
        return totalePagine;
    }
}
