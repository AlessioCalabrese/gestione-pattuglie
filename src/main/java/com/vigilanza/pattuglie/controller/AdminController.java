package com.vigilanza.pattuglie.controller;

import com.vigilanza.pattuglie.dto.GeocodificaResponse;
import com.vigilanza.pattuglie.dto.ObiettivoDTO;
import com.vigilanza.pattuglie.dto.PattugliaDTO;
import com.vigilanza.pattuglie.dto.UtenteDTO;
import com.vigilanza.pattuglie.entity.RuoloUtente;
import com.vigilanza.pattuglie.service.GeocodingService;
import com.vigilanza.pattuglie.service.ObiettivoService;
import com.vigilanza.pattuglie.service.PattugliaService;
import com.vigilanza.pattuglie.service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoint riservati al ruolo ADMIN (vedi SecurityConfig: /api/admin/** richiede ROLE_ADMIN).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UtenteService utenteService;
    private final PattugliaService pattugliaService;
    private final ObiettivoService obiettivoService;
    private final GeocodingService geocodingService;

    public AdminController(UtenteService utenteService, PattugliaService pattugliaService,
                            ObiettivoService obiettivoService, GeocodingService geocodingService) {
        this.utenteService = utenteService;
        this.pattugliaService = pattugliaService;
        this.obiettivoService = obiettivoService;
        this.geocodingService = geocodingService;
    }

    // ---- Utenti ----

    @GetMapping("/utenti")
    public List<UtenteDTO> listaUtenti() {
        return utenteService.findAll();
    }

    @PostMapping("/utenti")
    public UtenteDTO creaUtente(@RequestBody Map<String, String> body) {
        return utenteService.creaUtente(
                body.get("username"),
                body.get("password"),
                body.get("nome"),
                body.get("cognome"),
                RuoloUtente.valueOf(body.get("ruolo")),
                body.get("nfcTagId")
        );
    }

    @PutMapping("/utenti/{id}/abilitazione")
    public UtenteDTO impostaAbilitazione(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return utenteService.impostaAbilitato(id, body.get("abilitato"));
    }

    @DeleteMapping("/utenti/{id}")
    public ResponseEntity<Void> eliminaUtente(@PathVariable Long id) {
        utenteService.elimina(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Pattuglie ----

    @GetMapping("/pattuglie")
    public List<PattugliaDTO> listaPattuglie() {
        return pattugliaService.findTutteAttive();
    }

    @PostMapping("/pattuglie")
    public PattugliaDTO creaPattuglia(@RequestBody Map<String, String> body) {
        String tipoCarburante = body.get("tipoCarburante");
        return pattugliaService.crea(
                body.get("nome"),
                body.get("descrizione"),
                (tipoCarburante == null || tipoCarburante.isBlank())
                        ? null : com.vigilanza.pattuglie.entity.TipoCarburante.valueOf(tipoCarburante)
        );
    }

    @PutMapping("/pattuglie/{id}/stato")
    public PattugliaDTO impostaStatoPattuglia(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return pattugliaService.impostaAttiva(id, body.get("attiva"));
    }

    // ---- Geocodifica ----

    @GetMapping("/geocodifica")
    public GeocodificaResponse geocodifica(@RequestParam String indirizzo) {
        return geocodingService.geocodifica(indirizzo);
    }

    // ---- Obiettivi ----

    @GetMapping("/pattuglie/{pattugliaId}/obiettivi")
    public List<ObiettivoDTO> listaObiettivi(@PathVariable Long pattugliaId) {
        return obiettivoService.findPerAdmin(pattugliaId);
    }

    @PutMapping("/obiettivi/{id}")
    public ResponseEntity<?> aggiornaObiettivo(@PathVariable Long id,
                                                @RequestBody com.vigilanza.pattuglie.dto.NuovoObiettivoRequest request) {
        var obiettivo = obiettivoService.aggiorna(id, request);
        return ResponseEntity.ok(Map.of("id", obiettivo.getId()));
    }

    @PostMapping("/pattuglie/{pattugliaId}/obiettivi")
    public ResponseEntity<?> creaObiettivo(@PathVariable Long pattugliaId,
                                            @RequestBody com.vigilanza.pattuglie.dto.NuovoObiettivoRequest request) {
        var obiettivo = obiettivoService.crea(pattugliaId, request);
        return ResponseEntity.ok(Map.of("id", obiettivo.getId()));
    }
}
