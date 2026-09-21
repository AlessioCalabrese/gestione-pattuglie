package com.vigilanza.pattuglie.controller;

import com.vigilanza.pattuglie.dto.LogSistemaDTO;
import com.vigilanza.pattuglie.dto.PaginaDTO;
import com.vigilanza.pattuglie.service.LogSistemaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Consultazione del log di sistema, riservata agli amministratori (/api/admin/** richiede ROLE_ADMIN). */
@RestController
@RequestMapping("/api/admin/log")
public class LogSistemaController {

    private final LogSistemaService logSistemaService;

    public LogSistemaController(LogSistemaService logSistemaService) {
        this.logSistemaService = logSistemaService;
    }

    /** Eventi dal più recente. dal/al (formato yyyy-MM-dd, estremi inclusi) e tipo sono facoltativi. */
    @GetMapping
    public PaginaDTO<LogSistemaDTO> cerca(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate al,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int dimensione) {
        return logSistemaService.cerca(dal, al, tipo, pagina, dimensione);
    }

    @GetMapping("/tipi")
    public List<String> tipiEvento() {
        return logSistemaService.tipiEvento();
    }
}
