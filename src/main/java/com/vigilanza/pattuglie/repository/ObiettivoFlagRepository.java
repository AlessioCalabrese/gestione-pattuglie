package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.ObiettivoFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ObiettivoFlagRepository extends JpaRepository<ObiettivoFlag, Long> {

    List<ObiettivoFlag> findByObiettivoIdOrderByDataOraDesc(Long obiettivoId);

    List<ObiettivoFlag> findByObiettivoIdAndDataOraBetween(
        Long obiettivoId, LocalDateTime inizio, LocalDateTime fine);

    List<ObiettivoFlag> findByPattugliaIdAndDataOraBetween(
        Long pattugliaId, LocalDateTime inizio, LocalDateTime fine);
}
