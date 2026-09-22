package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.ObiettivoDTO;
import com.vigilanza.pattuglie.dto.PaginaDTO;
import com.vigilanza.pattuglie.dto.PattugliaDTO;
import com.vigilanza.pattuglie.dto.PattugliaRicercaDTO;
import com.vigilanza.pattuglie.entity.Obiettivo;
import com.vigilanza.pattuglie.entity.Pattuglia;
import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.ObiettivoRepository;
import com.vigilanza.pattuglie.repository.PattugliaRepository;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PattugliaService {

    /** Tetto alla dimensione di pagina richiesta dal client. */
    public static final int MAX_DIMENSIONE_PAGINA = 50;

    private final PattugliaRepository pattugliaRepository;
    private final ObiettivoRepository obiettivoRepository;
    private final UtenteRepository utenteRepository;
    private final LogSistemaService logSistemaService;

    public PattugliaService(PattugliaRepository pattugliaRepository, ObiettivoRepository obiettivoRepository,
                             UtenteRepository utenteRepository, LogSistemaService logSistemaService) {
        this.pattugliaRepository = pattugliaRepository;
        this.obiettivoRepository = obiettivoRepository;
        this.utenteRepository = utenteRepository;
        this.logSistemaService = logSistemaService;
    }

    public List<PattugliaDTO> findTutteAttive() {
        return pattugliaRepository.findByAttivaTrue().stream().map(this::toDto).toList();
    }

    /**
     * Pattuglie che l'utente può selezionare. Se ha scelto delle preferite vede solo quelle, a meno che
     * non chieda di vederle tutte ("tutte"); se non ne ha scelta nessuna vede tutte le pattuglie attive.
     * Ogni pattuglia indica se è tra le preferite. Restituisce solo i dati di base, senza gli obiettivi.
     */
    public List<PattugliaDTO> findSelezionabiliPerUtente(Long utenteId, boolean tutte) {
        Set<Long> preferite = new HashSet<>(pattugliaRepository.findIdAssociatePerUtente(utenteId));
        List<Pattuglia> pattuglie = (preferite.isEmpty() || tutte)
                ? pattugliaRepository.findByAttivaTrueOrderByNomeAsc()
                : pattugliaRepository.findSelezionabiliPerUtente(utenteId);
        return pattuglie.stream().map(p -> toDtoSenzaObiettivi(p, preferite.contains(p.getId()))).toList();
    }

    /** Tutte le pattuglie (anche disattivate) associate all'utente, per la vista amministratore. */
    public List<PattugliaDTO> findAssociateAUtente(Long utenteId) {
        return pattugliaRepository.findAssociatePerUtente(utenteId).stream()
                .map(p -> toDtoSenzaObiettivi(p, true)).toList();
    }

    /** Aggiunge o toglie una pattuglia dalle preferite dell'utente. */
    @Transactional
    public void impostaPreferita(Long utenteId, Long pattugliaId, boolean preferita) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));
        if (preferita) {
            utente.getPattuglieAbilitate().add(pattuglia);
        } else {
            utente.getPattuglieAbilitate().remove(pattuglia);
        }
    }

    /** Come {@link #impostaPreferita}, ma per conto di un amministratore: l'operazione finisce nel log di sistema. */
    @Transactional
    public void associaDaAmministratore(Long utenteId, Long pattugliaId, boolean associa, Long adminId, String indirizzoIp) {
        impostaPreferita(utenteId, pattugliaId, associa);

        Utente admin = utenteRepository.findById(adminId).orElse(null);
        Utente utente = utenteRepository.findById(utenteId).orElse(null);
        Pattuglia pattuglia = pattugliaRepository.findById(pattugliaId).orElse(null);
        logSistemaService.registra(admin, associa ? "ASSEGNA_PATTUGLIA" : "RIMUOVI_PATTUGLIA",
                "Pattuglia '" + (pattuglia == null ? pattugliaId : pattuglia.getNome()) + "' "
                        + (associa ? "associata all'utente '" : "rimossa dall'utente '")
                        + (utente == null ? utenteId : utente.getUsername()) + "'",
                indirizzoIp);
    }

    private PattugliaDTO toDtoSenzaObiettivi(Pattuglia p, boolean preferita) {
        PattugliaDTO dto = new PattugliaDTO(p.getId(), p.getNome(), p.getDescrizione(), p.isAttiva(), List.of());
        dto.setTipoCarburante(p.getTipoCarburante());
        dto.setPreferita(preferita);
        applicaGruppo(dto, p);
        return dto;
    }

    /** Riporta sul DTO il gruppo di accorpamento della pattuglia, se ne ha uno. */
    private void applicaGruppo(PattugliaDTO dto, Pattuglia p) {
        if (p.getGruppo() != null) {
            dto.setGruppoId(p.getGruppo().getId());
            dto.setGruppoNome(p.getGruppo().getNome());
        }
    }

    /**
     * Ricerca paginata per l'amministrazione: trova le pattuglie (anche disattivate) per nome, descrizione
     * o nome di un loro obiettivo. Testo vuoto = tutte. Per ogni pattuglia trovata indica anche quali
     * obiettivi corrispondono, così si capisce perché compare tra i risultati.
     */
    public PaginaDTO<PattugliaRicercaDTO> cerca(String testo, int pagina, int dimensione) {
        String q = testo == null ? "" : testo.trim();
        Pageable pageable = PageRequest.of(Math.max(pagina, 0), Math.min(Math.max(dimensione, 1), MAX_DIMENSIONE_PAGINA),
                Sort.by("nome").ascending());

        // Senza filtri: elenco completo paginato, senza passare dalla query di ricerca.
        Page<Pattuglia> risultati = q.isEmpty()
                ? pattugliaRepository.findAll(pageable)
                : pattugliaRepository.cerca(patternLike(q), pageable);

        Map<Long, List<String>> obiettiviTrovati = Map.of();
        if (!q.isEmpty() && !risultati.isEmpty()) {
            List<Long> ids = risultati.getContent().stream().map(Pattuglia::getId).toList();
            obiettiviTrovati = obiettivoRepository
                    .findByPattugliaIdInAndAttivoTrueAndNomeContainingIgnoreCase(ids, q).stream()
                    .collect(Collectors.groupingBy(o -> o.getPattuglia().getId(),
                            Collectors.mapping(Obiettivo::getNome, Collectors.toList())));
        }

        Map<Long, List<String>> corrispondenze = obiettiviTrovati;
        return PaginaDTO.da(risultati, p -> new PattugliaRicercaDTO(p.getId(), p.getNome(), p.getDescrizione(),
                p.isAttiva(), p.getTipoCarburante(), corrispondenze.getOrDefault(p.getId(), List.of())));
    }

    /** Testo cercato nel formato per LIKE: minuscolo, con % ai lati e i caratteri speciali (! % _) preceduti da '!'. */
    private static String patternLike(String testo) {
        String escaped = testo.toLowerCase(Locale.ROOT)
                .replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return "%" + escaped + "%";
    }

    public PattugliaDTO crea(String nome, String descrizione,
                              com.vigilanza.pattuglie.entity.TipoCarburante tipoCarburante) {
        Pattuglia pattuglia = new Pattuglia(nome, descrizione);
        if (tipoCarburante != null) {
            pattuglia.setTipoCarburante(tipoCarburante);
        }
        return toDto(pattugliaRepository.save(pattuglia));
    }

    public PattugliaDTO impostaAttiva(Long id, boolean attiva) {
        Pattuglia pattuglia = pattugliaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pattuglia non trovata"));
        pattuglia.setAttiva(attiva);
        return toDto(pattugliaRepository.save(pattuglia));
    }

    private PattugliaDTO toDto(Pattuglia p) {
        List<ObiettivoDTO> obiettivi = p.getObiettivi().stream()
                .filter(Obiettivo::isAttivo)
                .map(this::toObiettivoDto)
                .toList();
        PattugliaDTO dto = new PattugliaDTO(p.getId(), p.getNome(), p.getDescrizione(),
                p.isAttiva(), obiettivi);
        dto.setTipoCarburante(p.getTipoCarburante());
        applicaGruppo(dto, p);
        return dto;
    }

    private ObiettivoDTO toObiettivoDto(Obiettivo o) {
        ObiettivoDTO dto = new ObiettivoDTO(o.getId(), o.getPattuglia().getId(), o.getNome(),
                o.getVia(), o.getComune(), o.getLatitudine(), o.getLongitudine(),
                o.getOrdineVisita(), o.isAttivo());
        dto.setPriorita(o.isPriorita());
        dto.setTipoObiettivo(o.getTipoObiettivo());
        dto.setFasceOrarie(o.getFasceOrarie().stream()
                .map(f -> new com.vigilanza.pattuglie.dto.FasciaOrariaDTO(
                        f.getGiorno(), f.getOraInizio(), f.getOraFine(), f.getRipetizioniRichieste()))
                .toList());
        return dto;
    }
}
