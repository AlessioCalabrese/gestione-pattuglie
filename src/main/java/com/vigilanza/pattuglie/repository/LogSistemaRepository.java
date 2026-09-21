package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.LogSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {

    /**
     * Eventi con data/ora nell'intervallo [dal, al) e, se "tipo" non è vuoto, del tipo indicato.
     * L'ordinamento (dal più recente) è dato dal Pageable.
     */
    @Query(value = """
            SELECT l FROM LogSistema l LEFT JOIN FETCH l.utente
            WHERE l.dataOra >= :dal AND l.dataOra < :al
              AND (:tipo = '' OR l.tipoEvento = :tipo)
            """,
            countQuery = """
            SELECT COUNT(l) FROM LogSistema l
            WHERE l.dataOra >= :dal AND l.dataOra < :al
              AND (:tipo = '' OR l.tipoEvento = :tipo)
            """)
    Page<LogSistema> cerca(@Param("dal") LocalDateTime dal, @Param("al") LocalDateTime al,
                           @Param("tipo") String tipo, Pageable pageable);

    /** Tipi di evento presenti nel log, per il filtro. */
    @Query("SELECT DISTINCT l.tipoEvento FROM LogSistema l ORDER BY l.tipoEvento")
    List<String> findTipiEvento();
}
