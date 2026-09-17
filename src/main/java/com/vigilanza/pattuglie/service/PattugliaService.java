package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.ObiettivoDTO;
import com.vigilanza.pattuglie.dto.PattugliaDTO;
import com.vigilanza.pattuglie.entity.Obiettivo;
import com.vigilanza.pattuglie.entity.Pattuglia;
import com.vigilanza.pattuglie.repository.PattugliaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PattugliaService {

    private final PattugliaRepository pattugliaRepository;

    public PattugliaService(PattugliaRepository pattugliaRepository) {
        this.pattugliaRepository = pattugliaRepository;
    }

    public List<PattugliaDTO> findTutteAttive() {
        return pattugliaRepository.findByAttivaTrue().stream().map(this::toDto).toList();
    }

    public List<PattugliaDTO> findSelezionabiliPerUtente(Long utenteId) {
        return pattugliaRepository.findSelezionabiliPerUtente(utenteId).stream().map(this::toDto).toList();
    }

    public PattugliaDTO crea(String nome, String descrizione, String veicoloTarga,
                              java.math.BigDecimal consumoMedioL100Km,
                              com.vigilanza.pattuglie.entity.TipoCarburante tipoCarburante) {
        Pattuglia pattuglia = new Pattuglia(nome, descrizione, veicoloTarga);
        if (consumoMedioL100Km != null) {
            pattuglia.setConsumoMedioL100Km(consumoMedioL100Km);
        }
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
                p.getVeicoloTarga(), p.isAttiva(), obiettivi);
        dto.setConsumoMedioL100Km(p.getConsumoMedioL100Km());
        dto.setTipoCarburante(p.getTipoCarburante());
        return dto;
    }

    private ObiettivoDTO toObiettivoDto(Obiettivo o) {
        return new ObiettivoDTO(o.getId(), o.getPattuglia().getId(), o.getNome(),
                o.getIndirizzo(), o.getLatitudine(), o.getLongitudine(),
                o.getOrdineVisita(), o.isAttivo());
    }
}
