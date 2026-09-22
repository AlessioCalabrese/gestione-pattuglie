package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.Obiettivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ObiettivoRepository extends JpaRepository<Obiettivo, Long> {
    List<Obiettivo> findByPattugliaIdAndAttivoTrueOrderByOrdineVisitaAsc(Long pattugliaId);

    /** Come sopra, ma per più pattuglie insieme (usata quando la pattuglia è accorpata ad altre). */
    List<Obiettivo> findByPattugliaIdInAndAttivoTrueOrderByOrdineVisitaAsc(Collection<Long> pattugliaIds);

    List<Obiettivo> findByAttivoTrue();

    Page<Obiettivo> findByPattugliaIdAndAttivoTrue(Long pattugliaId, Pageable pageable);

    /** Obiettivi attivi delle pattuglie indicate il cui nome contiene il testo (senza distinzione tra maiuscole e minuscole). */
    List<Obiettivo> findByPattugliaIdInAndAttivoTrueAndNomeContainingIgnoreCase(Collection<Long> pattugliaIds, String testo);
}
