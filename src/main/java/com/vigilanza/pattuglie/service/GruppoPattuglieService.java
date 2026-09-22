package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.GruppoPattuglieDTO;
import com.vigilanza.pattuglie.dto.MembroGruppoDTO;
import com.vigilanza.pattuglie.entity.GruppoPattuglie;
import com.vigilanza.pattuglie.entity.Pattuglia;
import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.GruppoPattuglieRepository;
import com.vigilanza.pattuglie.repository.PattugliaRepository;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Accorpamento di pattuglie configurato dall'amministratore: le pattuglie di uno stesso gruppo vedono e
 * possono flaggare anche gli obiettivi delle altre pattuglie del gruppo (usato da {@link ObiettivoService}).
 * Una pattuglia appartiene al massimo a un gruppo: aggiungerla a uno nuovo la rimuove automaticamente
 * dal precedente.
 */
@Service
public class GruppoPattuglieService {

    private final GruppoPattuglieRepository gruppoRepository;
    private final PattugliaRepository pattugliaRepository;
    private final UtenteRepository utenteRepository;
    private final LogSistemaService logSistemaService;

    public GruppoPattuglieService(GruppoPattuglieRepository gruppoRepository, PattugliaRepository pattugliaRepository,
                                   UtenteRepository utenteRepository, LogSistemaService logSistemaService) {
        this.gruppoRepository = gruppoRepository;
        this.pattugliaRepository = pattugliaRepository;
        this.utenteRepository = utenteRepository;
        this.logSistemaService = logSistemaService;
    }

    public List<GruppoPattuglieDTO> findTutti() {
        return gruppoRepository.findAllByOrderByNomeAsc().stream().map(this::toDto).toList();
    }

    @Transactional
    public GruppoPattuglieDTO crea(String nome, Long adminId, String indirizzoIp) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Indica un nome per il gruppo");
        }
        GruppoPattuglie gruppo = gruppoRepository.save(new GruppoPattuglie(nome.trim()));
        registraLog(adminId, "CREA_GRUPPO_PATTUGLIE", "Creato il gruppo di pattuglie '" + gruppo.getNome() + "'", indirizzoIp);
        return toDto(gruppo);
    }

    @Transactional
    public void elimina(Long gruppoId, Long adminId, String indirizzoIp) {
        GruppoPattuglie gruppo = gruppoRepository.findById(gruppoId)
                .orElseThrow(() -> new IllegalArgumentException("Gruppo non trovato"));
        for (Pattuglia p : pattugliaRepository.findByGruppoIdOrderByNomeAsc(gruppoId)) {
            p.setGruppo(null);
        }
        gruppoRepository.delete(gruppo);
        registraLog(adminId, "ELIMINA_GRUPPO_PATTUGLIE", "Eliminato il gruppo di pattuglie '" + gruppo.getNome() + "'", indirizzoIp);
    }

    /** Aggiunge la pattuglia al gruppo. Se apparteneva già a un altro gruppo, ne viene rimossa. */
    @Transactional
    public GruppoPattuglieDTO aggiungiMembro(Long gruppoId, Long pattugliaId, Long adminId, String indirizzoIp) {
        GruppoPattuglie gruppo = gruppoRepository.findById(gruppoId)
                .orElseThrow(() -> new IllegalArgumentException("Gruppo non trovato"));
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));

        String gruppoPrecedente = pattuglia.getGruppo() == null ? null : pattuglia.getGruppo().getNome();
        pattuglia.setGruppo(gruppo);

        registraLog(adminId, "ASSEGNA_GRUPPO_PATTUGLIA",
                "Pattuglia '" + pattuglia.getNome() + "' aggiunta al gruppo '" + gruppo.getNome() + "'"
                        + (gruppoPrecedente != null ? " (rimossa dal gruppo '" + gruppoPrecedente + "')" : ""),
                indirizzoIp);
        return toDto(gruppo);
    }

    @Transactional
    public GruppoPattuglieDTO rimuoviMembro(Long gruppoId, Long pattugliaId, Long adminId, String indirizzoIp) {
        GruppoPattuglie gruppo = gruppoRepository.findById(gruppoId)
                .orElseThrow(() -> new IllegalArgumentException("Gruppo non trovato"));
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));
        if (pattuglia.getGruppo() == null || !pattuglia.getGruppo().getId().equals(gruppoId)) {
            throw new IllegalArgumentException("La pattuglia non fa parte di questo gruppo");
        }

        pattuglia.setGruppo(null);
        registraLog(adminId, "RIMUOVI_GRUPPO_PATTUGLIA",
                "Pattuglia '" + pattuglia.getNome() + "' rimossa dal gruppo '" + gruppo.getNome() + "'", indirizzoIp);
        return toDto(gruppo);
    }

    /**
     * Id delle pattuglie di cui considerare gli obiettivi per la pattuglia indicata: se appartiene a un
     * gruppo, tutte le pattuglie di quel gruppo (compresa lei stessa); altrimenti solo lei stessa.
     */
    public List<Long> idPattugliePerAccorpamento(Long pattugliaId) {
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));
        if (pattuglia.getGruppo() == null) {
            return List.of(pattugliaId);
        }
        return pattugliaRepository.findByGruppoIdOrderByNomeAsc(pattuglia.getGruppo().getId()).stream()
                .map(Pattuglia::getId).toList();
    }

    /** Vero se le due pattuglie sono la stessa o appartengono allo stesso gruppo di accorpamento. */
    public boolean stessoAccorpamento(Long pattugliaIdA, Long pattugliaIdB) {
        if (pattugliaIdA.equals(pattugliaIdB)) {
            return true;
        }
        return idPattugliePerAccorpamento(pattugliaIdB).contains(pattugliaIdA);
    }

    private void registraLog(Long adminId, String tipoEvento, String descrizione, String indirizzoIp) {
        Utente admin = adminId == null ? null : utenteRepository.findById(adminId).orElse(null);
        logSistemaService.registra(admin, tipoEvento, descrizione, indirizzoIp);
    }

    private GruppoPattuglieDTO toDto(GruppoPattuglie gruppo) {
        List<MembroGruppoDTO> membri = pattugliaRepository.findByGruppoIdOrderByNomeAsc(gruppo.getId()).stream()
                .map(p -> new MembroGruppoDTO(p.getId(), p.getNome(), p.getDescrizione(), p.isAttiva()))
                .toList();
        return new GruppoPattuglieDTO(gruppo.getId(), gruppo.getNome(), membri);
    }
}
