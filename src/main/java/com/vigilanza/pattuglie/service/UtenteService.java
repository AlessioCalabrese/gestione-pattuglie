package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.UtenteDTO;
import com.vigilanza.pattuglie.entity.RuoloUtente;
import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public UtenteService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UtenteDTO> findAll() {
        return utenteRepository.findAll().stream().map(this::toDto).toList();
    }

    public UtenteDTO creaUtente(String username, String password, String nome,
                                 String cognome, RuoloUtente ruolo, String nfcTagId) {
        if (utenteRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username già esistente");
        }
        if (nfcTagId != null && !nfcTagId.isBlank() && utenteRepository.findByNfcTagId(nfcTagId).isPresent()) {
            throw new IllegalArgumentException("Tag NFC già assegnato a un altro utente");
        }
        Utente utente = new Utente(username, passwordEncoder.encode(password), nome, cognome, ruolo);
        utente.setNfcTagId((nfcTagId == null || nfcTagId.isBlank()) ? null : nfcTagId);
        return toDto(utenteRepository.save(utente));
    }

    public UtenteDTO impostaAbilitato(Long id, boolean abilitato) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        utente.setAbilitato(abilitato);
        return toDto(utenteRepository.save(utente));
    }

    public void elimina(Long id) {
        // NOTA: valutare vincolo di integrità referenziale — se l'utente ha
        // storico su obiettivo_flag/log_sistema, preferire la disabilitazione
        // all'eliminazione definitiva (come fatto nel progetto parco auto).
        utenteRepository.deleteById(id);
    }

    private UtenteDTO toDto(Utente u) {
        UtenteDTO dto = new UtenteDTO(u.getId(), u.getUsername(), u.getNome(), u.getCognome(),
                u.getRuolo(), u.isAbilitato());
        dto.setNfcTagId(u.getNfcTagId());
        return dto;
    }
}
