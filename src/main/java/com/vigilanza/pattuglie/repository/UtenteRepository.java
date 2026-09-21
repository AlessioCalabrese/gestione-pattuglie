package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByUsername(String username);
    Optional<Utente> findByNfcTagId(String nfcTagId);

    /**
     * Utenti il cui tag NFC coincide con il codice indicato, senza distinguere maiuscole/minuscole e
     * ignorando ':', '-' e spazi. "codice" deve essere già normalizzato (vedi CodiceNfc).
     */
    @Query("SELECT u FROM Utente u WHERE u.nfcTagId IS NOT NULL AND "
            + "LOWER(REPLACE(REPLACE(REPLACE(u.nfcTagId, ':', ''), '-', ''), ' ', '')) = :codice")
    List<Utente> findByNfcTagIdNormalizzato(String codice);
}
