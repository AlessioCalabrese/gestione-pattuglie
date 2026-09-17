package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.entity.LogSistema;
import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.LogSistemaRepository;
import org.springframework.stereotype.Service;

@Service
public class LogSistemaService {

    private final LogSistemaRepository logSistemaRepository;

    public LogSistemaService(LogSistemaRepository logSistemaRepository) {
        this.logSistemaRepository = logSistemaRepository;
    }

    public void registra(Utente utente, String tipoEvento, String descrizione, String indirizzoIp) {
        LogSistema log = new LogSistema(utente, tipoEvento, descrizione, indirizzoIp);
        logSistemaRepository.save(log);
    }
}
