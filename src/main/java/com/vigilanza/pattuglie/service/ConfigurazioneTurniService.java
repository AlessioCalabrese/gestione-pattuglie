package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.ConfigurazioneTurniDTO;
import com.vigilanza.pattuglie.entity.ConfigurazioneTurni;
import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.ConfigurazioneTurniRepository;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

/**
 * Orari dei tre turni standard (Mattina, Pomeriggio, Notte) usati come proposta rapida per le fasce
 * orarie degli obiettivi. Riga singola in DB, modificabile dall'amministratore.
 */
@Service
public class ConfigurazioneTurniService {

    private record Turno(String nome, LocalTime inizio, LocalTime fine) {
        boolean notturno() {
            return fine.isBefore(inizio);
        }
    }

    private final ConfigurazioneTurniRepository repository;
    private final UtenteRepository utenteRepository;
    private final LogSistemaService logSistemaService;

    public ConfigurazioneTurniService(ConfigurazioneTurniRepository repository, UtenteRepository utenteRepository,
                                       LogSistemaService logSistemaService) {
        this.repository = repository;
        this.utenteRepository = utenteRepository;
        this.logSistemaService = logSistemaService;
    }

    public ConfigurazioneTurniDTO leggi() {
        return toDto(trovaOInizializza());
    }

    @Transactional
    public ConfigurazioneTurniDTO aggiorna(ConfigurazioneTurniDTO richiesta, Long adminId, String indirizzoIp) {
        valida(richiesta);

        ConfigurazioneTurni c = trovaOInizializza();
        c.setMattinaInizio(richiesta.getMattinaInizio());
        c.setMattinaFine(richiesta.getMattinaFine());
        c.setPomeriggioInizio(richiesta.getPomeriggioInizio());
        c.setPomeriggioFine(richiesta.getPomeriggioFine());
        c.setNotteInizio(richiesta.getNotteInizio());
        c.setNotteFine(richiesta.getNotteFine());
        repository.save(c);

        Utente admin = adminId == null ? null : utenteRepository.findById(adminId).orElse(null);
        logSistemaService.registra(admin, "MODIFICA_TURNI",
                "Orari turni aggiornati: Mattina " + c.getMattinaInizio() + "-" + c.getMattinaFine()
                        + ", Pomeriggio " + c.getPomeriggioInizio() + "-" + c.getPomeriggioFine()
                        + ", Notte " + c.getNotteInizio() + "-" + c.getNotteFine() + " (del giorno dopo)",
                indirizzoIp);
        return toDto(c);
    }

    private ConfigurazioneTurni trovaOInizializza() {
        return repository.findById(ConfigurazioneTurni.ID_UNICO).orElseGet(() -> repository.save(new ConfigurazioneTurni()));
    }

    /**
     * Verifica che i tre turni non si sovrappongano nell'arco delle 24 ore. La Notte attraversa la
     * mezzanotte per definizione quando la sua fine precede il suo inizio (es. 22:00–06:00): in quel caso
     * la sua "coda" (dalla mezzanotte fino all'ora di fine) non deve sovrapporre l'inizio degli altri due
     * turni il giorno successivo. I turni possono toccarsi (l'uno finisce esattamente quando l'altro
     * inizia) senza che sia considerato un errore.
     */
    private static void valida(ConfigurazioneTurniDTO r) {
        List<Turno> turni = List.of(
                new Turno("Mattina", richiedi(r.getMattinaInizio(), "Mattina", "inizio"), richiedi(r.getMattinaFine(), "Mattina", "fine")),
                new Turno("Pomeriggio", richiedi(r.getPomeriggioInizio(), "Pomeriggio", "inizio"), richiedi(r.getPomeriggioFine(), "Pomeriggio", "fine")),
                new Turno("Notte", richiedi(r.getNotteInizio(), "Notte", "inizio"), richiedi(r.getNotteFine(), "Notte", "fine"))
        );

        for (Turno t : turni) {
            if (t.inizio().equals(t.fine())) {
                throw new IllegalArgumentException("L'ora di inizio e l'ora di fine del turno " + t.nome() + " non possono coincidere");
            }
        }

        // Sovrapposizioni nello stesso giorno: un turno notturno occupa, ai fini di questo confronto,
        // tutto il resto della giornata (fino a mezzanotte); la sua coda nel giorno dopo è verificata a parte.
        for (int i = 0; i < turni.size(); i++) {
            for (int j = i + 1; j < turni.size(); j++) {
                Turno a = turni.get(i);
                Turno b = turni.get(j);
                LocalTime fineA = a.notturno() ? LocalTime.MAX : a.fine();
                LocalTime fineB = b.notturno() ? LocalTime.MAX : b.fine();
                if (a.inizio().isBefore(fineB) && b.inizio().isBefore(fineA)) {
                    throw new IllegalArgumentException("I turni " + a.nome() + " e " + b.nome() + " si sovrappongono");
                }
            }
        }

        // Coda del turno notturno (dalla mezzanotte alla sua ora di fine, il giorno dopo): non deve
        // sovrapporre l'inizio di nessun altro turno, che il giorno dopo si ripete alla stessa ora.
        for (Turno notturno : turni) {
            if (!notturno.notturno()) {
                continue;
            }
            for (Turno altro : turni) {
                if (altro == notturno) {
                    continue;
                }
                if (altro.inizio().isBefore(notturno.fine())) {
                    throw new IllegalArgumentException("Il turno " + notturno.nome() + ", che termina alle "
                            + notturno.fine() + " del giorno successivo, si sovrappone con l'inizio del turno " + altro.nome());
                }
            }
        }
    }

    private static LocalTime richiedi(LocalTime valore, String turno, String estremo) {
        if (valore == null) {
            throw new IllegalArgumentException("Indica l'ora di " + estremo + " del turno " + turno);
        }
        return valore;
    }

    private static ConfigurazioneTurniDTO toDto(ConfigurazioneTurni c) {
        return new ConfigurazioneTurniDTO(c.getMattinaInizio(), c.getMattinaFine(),
                c.getPomeriggioInizio(), c.getPomeriggioFine(), c.getNotteInizio(), c.getNotteFine());
    }
}
