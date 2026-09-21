package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.LogSistemaDTO;
import com.vigilanza.pattuglie.dto.PaginaDTO;
import com.vigilanza.pattuglie.entity.LogSistema;
import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.LogSistemaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogSistemaService {

    /** Lunghezza massima della descrizione (colonna log_sistema.descrizione). */
    private static final int MAX_DESCRIZIONE = 500;

    // Estremi usati quando l'intervallo di date non è indicato (entro l'intervallo supportato da SQL Server DATETIME)
    private static final LocalDateTime INIZIO_DEI_TEMPI = LocalDateTime.of(1970, 1, 1, 0, 0);
    private static final LocalDateTime FINE_DEI_TEMPI = LocalDateTime.of(2999, 1, 1, 0, 0);

    private final LogSistemaRepository logSistemaRepository;

    public LogSistemaService(LogSistemaRepository logSistemaRepository) {
        this.logSistemaRepository = logSistemaRepository;
    }

    public void registra(Utente utente, String tipoEvento, String descrizione, String indirizzoIp) {
        if (descrizione != null && descrizione.length() > MAX_DESCRIZIONE) {
            descrizione = descrizione.substring(0, MAX_DESCRIZIONE - 1) + "…";
        }
        LogSistema log = new LogSistema(utente, tipoEvento, descrizione, indirizzoIp);
        logSistemaRepository.save(log);
    }

    /**
     * Eventi del log dal più recente, paginati, filtrabili per intervallo di date (estremi inclusi, entrambi
     * facoltativi) e per tipo di evento (facoltativo).
     */
    public PaginaDTO<LogSistemaDTO> cerca(LocalDate dal, LocalDate al, String tipoEvento, int pagina, int dimensione) {
        if (dal != null && al != null && dal.isAfter(al)) {
            throw new IllegalArgumentException("La data iniziale non può essere successiva a quella finale");
        }
        LocalDateTime da = dal == null ? INIZIO_DEI_TEMPI : dal.atStartOfDay();
        LocalDateTime a = al == null ? FINE_DEI_TEMPI : al.plusDays(1).atStartOfDay(); // fine giornata inclusa
        String tipo = tipoEvento == null ? "" : tipoEvento.trim();

        var pageable = PageRequest.of(Math.max(pagina, 0),
                Math.min(Math.max(dimensione, 1), PattugliaService.MAX_DIMENSIONE_PAGINA),
                Sort.by(Sort.Order.desc("dataOra"), Sort.Order.desc("id")));
        return PaginaDTO.da(logSistemaRepository.cerca(da, a, tipo, pageable), LogSistemaDTO::new);
    }

    public List<String> tipiEvento() {
        return logSistemaRepository.findTipiEvento();
    }
}
