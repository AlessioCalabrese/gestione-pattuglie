package com.vigilanza.pattuglie.controller;

import com.vigilanza.pattuglie.dto.PosizioneDTO;
import com.vigilanza.pattuglie.service.GeolocalizzazioneIpService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Posizione di ripiego per i dispositivi senza GPS (utenti autenticati, qualunque ruolo). */
@RestController
@RequestMapping("/api/posizione")
public class PosizioneController {

    private final GeolocalizzazioneIpService geolocalizzazioneIpService;

    public PosizioneController(GeolocalizzazioneIpService geolocalizzazioneIpService) {
        this.geolocalizzazioneIpService = geolocalizzazioneIpService;
    }

    /** Stima la posizione del chiamante dall'indirizzo IP della sua connessione. */
    @GetMapping("/da-rete")
    public PosizioneDTO daRete(HttpServletRequest request) {
        return geolocalizzazioneIpService.stima(request.getHeader("X-Forwarded-For"), request.getRemoteAddr());
    }
}
