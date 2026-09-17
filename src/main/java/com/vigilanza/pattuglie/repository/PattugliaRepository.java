package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.Pattuglia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PattugliaRepository extends JpaRepository<Pattuglia, Long> {
    List<Pattuglia> findByAttivaTrue();

    // Pattuglie selezionabili da un dato utente (tramite tabella utente_pattuglia)
    @org.springframework.data.jpa.repository.Query(
        "SELECT p FROM Pattuglia p JOIN p.utentiAbilitati u " +
        "WHERE p.attiva = true AND u.id = :utenteId"
    )
    List<Pattuglia> findSelezionabiliPerUtente(Long utenteId);
}
