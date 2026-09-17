package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.Obiettivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObiettivoRepository extends JpaRepository<Obiettivo, Long> {
    List<Obiettivo> findByPattugliaIdAndAttivoTrueOrderByOrdineVisitaAsc(Long pattugliaId);
}
