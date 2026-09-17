package com.vigilanza.pattuglie.repository;

import com.vigilanza.pattuglie.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByUsername(String username);
    Optional<Utente> findByNfcTagId(String nfcTagId);
}
