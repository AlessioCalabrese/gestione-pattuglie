package com.vigilanza.pattuglie.controller;

import com.vigilanza.pattuglie.dto.FlagObiettivoRequest;
import com.vigilanza.pattuglie.dto.ObiettivoDTO;
import com.vigilanza.pattuglie.dto.PattugliaDTO;
import com.vigilanza.pattuglie.security.AuthenticatedUser;
import com.vigilanza.pattuglie.service.ObiettivoService;
import com.vigilanza.pattuglie.service.PattugliaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoint per l'operatività quotidiana della pattuglia (ruolo PATTUGLIA o ADMIN).
 */
@RestController
@RequestMapping("/api")
public class PattugliaOperativaController {

    private final PattugliaService pattugliaService;
    private final ObiettivoService obiettivoService;

    public PattugliaOperativaController(PattugliaService pattugliaService, ObiettivoService obiettivoService) {
        this.pattugliaService = pattugliaService;
        this.obiettivoService = obiettivoService;
    }

    /**
     * Pattuglie che l'utente loggato può selezionare: le sue preferite se ne ha scelte (a meno di tutte=true),
     * altrimenti tutte le pattuglie attive.
     */
    @GetMapping("/pattuglie/mie")
    public List<PattugliaDTO> pattuglieSelezionabili(@RequestParam(defaultValue = "false") boolean tutte) {
        Long utenteId = AuthenticatedUser.getUtenteId();
        return pattugliaService.findSelezionabiliPerUtente(utenteId, tutte);
    }

    /** Aggiunge o toglie una pattuglia dalle preferite dell'utente loggato. Body: {"preferita": true|false}. */
    @PutMapping("/pattuglie/{pattugliaId}/preferita")
    public ResponseEntity<Void> impostaPreferita(@PathVariable Long pattugliaId, @RequestBody Map<String, Boolean> body) {
        Boolean preferita = body.get("preferita");
        if (preferita == null) {
            throw new IllegalArgumentException("Campo 'preferita' mancante");
        }
        pattugliaService.impostaPreferita(AuthenticatedUser.getUtenteId(), pattugliaId, preferita);
        return ResponseEntity.ok().build();
    }

    /** Lista obiettivi della pattuglia selezionata, con stato del flag odierno. */
    @GetMapping("/pattuglie/{pattugliaId}/obiettivi")
    public List<ObiettivoDTO> obiettiviPattuglia(@PathVariable Long pattugliaId) {
        return obiettivoService.findByPattuglia(pattugliaId);
    }

    /** Registra il flag su un obiettivo, con le coordinate rilevate dal client. */
    @PostMapping("/pattuglie/{pattugliaId}/obiettivi/flag")
    public ResponseEntity<Void> flagObiettivo(@PathVariable Long pattugliaId,
                                               @RequestBody FlagObiettivoRequest request,
                                               HttpServletRequest httpRequest) {
        Long utenteId = AuthenticatedUser.getUtenteId();
        obiettivoService.flagObiettivo(utenteId, pattugliaId, request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok().build();
    }

    /**
     * Ricalcola l'ordine di visita ottimale degli obiettivi a partire dalla
     * posizione corrente della pattuglia (o dalla sede, se non disponibile),
     * restituendo anche la stima del risparmio carburante rispetto all'ordine precedente.
     */
    @PostMapping("/pattuglie/{pattugliaId}/obiettivi/ottimizza-rotta")
    public com.vigilanza.pattuglie.dto.OttimizzazioneRottaResponse ottimizzaRotta(
            @PathVariable Long pattugliaId, @RequestBody Map<String, Double> posizione) {
        double lat = posizione.get("latitudine");
        double lng = posizione.get("longitudine");
        return obiettivoService.ottimizzaRotta(pattugliaId, lat, lng);
    }
}
