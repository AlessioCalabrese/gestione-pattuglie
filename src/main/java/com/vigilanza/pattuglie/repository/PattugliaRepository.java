package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.Pattuglia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PattugliaRepository extends JpaRepository<Pattuglia, Long> {
    List<Pattuglia> findByAttivaTrue();

    List<Pattuglia> findByAttivaTrueOrderByNomeAsc();

    // Pattuglie attive tra quelle associate a un dato utente (tramite tabella utente_pattuglia)
    @Query(
        "SELECT p FROM Pattuglia p JOIN p.utentiAbilitati u " +
        "WHERE p.attiva = true AND u.id = :utenteId ORDER BY p.nome"
    )
    List<Pattuglia> findSelezionabiliPerUtente(Long utenteId);

    // Id delle pattuglie (attive o no) associate all'utente: vuoto = nessuna preferenza espressa
    @Query("SELECT p.id FROM Pattuglia p JOIN p.utentiAbilitati u WHERE u.id = :utenteId")
    List<Long> findIdAssociatePerUtente(Long utenteId);

    // Tutte le pattuglie (attive o no) associate all'utente, per la vista amministratore
    @Query("SELECT p FROM Pattuglia p JOIN p.utentiAbilitati u WHERE u.id = :utenteId ORDER BY p.nome")
    List<Pattuglia> findAssociatePerUtente(Long utenteId);

    // Pattuglie (attive o no) che appartengono a un dato gruppo di accorpamento
    List<Pattuglia> findByGruppoIdOrderByNomeAsc(Long gruppoId);

    /**
     * Ricerca amministrativa: pattuglie (anche disattivate) il cui nome o descrizione, oppure il nome
     * di uno dei loro obiettivi attivi, contiene il testo cercato. "pattern" è già nel formato LIKE
     * (minuscolo, con % ai lati e i caratteri speciali preceduti da '!', vedi PattugliaService).
     */
    @Query("""
            SELECT p FROM Pattuglia p
            WHERE LOWER(p.nome) LIKE :pattern ESCAPE '!'
               OR LOWER(COALESCE(p.descrizione, '')) LIKE :pattern ESCAPE '!'
               OR EXISTS (SELECT o.id FROM Obiettivo o
                          WHERE o.pattuglia = p AND o.attivo = true
                            AND LOWER(o.nome) LIKE :pattern ESCAPE '!')
            """)
    Page<Pattuglia> cerca(@Param("pattern") String pattern, Pageable pageable);
}
