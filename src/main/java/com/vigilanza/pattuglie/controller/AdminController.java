package com.vigilanza.pattuglie.controller;

import com.vigilanza.pattuglie.dto.GeocodificaResponse;
import com.vigilanza.pattuglie.dto.ObiettivoDTO;
import com.vigilanza.pattuglie.dto.PaginaDTO;
import com.vigilanza.pattuglie.dto.PattugliaDTO;
import com.vigilanza.pattuglie.dto.PattugliaRicercaDTO;
import com.vigilanza.pattuglie.dto.UtenteDTO;
import com.vigilanza.pattuglie.entity.RuoloUtente;
import com.vigilanza.pattuglie.dto.StatoAggiornamentoCoordinateDTO;
import com.vigilanza.pattuglie.security.AuthenticatedUser;
import com.vigilanza.pattuglie.service.AggiornamentoCoordinateService;
import com.vigilanza.pattuglie.service.GeocodingService;
import com.vigilanza.pattuglie.service.ObiettivoService;
import com.vigilanza.pattuglie.service.PattugliaService;
import com.vigilanza.pattuglie.service.UtenteService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AggiornamentoCoordinateService aggiornamentoCoordinateService;

    public AdminController(UtenteService utenteService, PattugliaService pattugliaService,
                            ObiettivoService obiettivoService, GeocodingService geocodingService,
                            AggiornamentoCoordinateService aggiornamentoCoordinateService) {
        this.utenteService = utenteService;
        this.pattugliaService = pattugliaService;
        this.obiettivoService = obiettivoService;
        this.geocodingService = geocodingService;
        this.aggiornamentoCoordinateService = aggiornamentoCoordinateService;
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

    /** Pattuglie associate (preferite) a un utente. */
    @GetMapping("/utenti/{id}/pattuglie")
    public List<PattugliaDTO> pattuglieDiUtente(@PathVariable Long id) {
        return pattugliaService.findAssociateAUtente(id);
    }

    @PutMapping("/utenti/{id}/pattuglie/{pattugliaId}")
    public ResponseEntity<Void> associaPattuglia(@PathVariable Long id, @PathVariable Long pattugliaId,
                                                  HttpServletRequest httpRequest) {
        pattugliaService.associaDaAmministratore(id, pattugliaId, true, AuthenticatedUser.getUtenteId(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/utenti/{id}/pattuglie/{pattugliaId}")
    public ResponseEntity<Void> rimuoviPattuglia(@PathVariable Long id, @PathVariable Long pattugliaId,
                                                  HttpServletRequest httpRequest) {
        pattugliaService.associaDaAmministratore(id, pattugliaId, false, AuthenticatedUser.getUtenteId(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
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

    /** Ricerca paginata per nome/descrizione della pattuglia o nome di un suo obiettivo (testo vuoto = tutte). */
    @GetMapping("/pattuglie/ricerca")
    public PaginaDTO<PattugliaRicercaDTO> cercaPattuglie(@RequestParam(defaultValue = "") String q,
                                                          @RequestParam(defaultValue = "0") int pagina,
                                                          @RequestParam(defaultValue = "10") int dimensione) {
        return pattugliaService.cerca(q, pagina, dimensione);
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

    /** Avvia in background il ricalcolo delle coordinate di tutti gli obiettivi attivi dai loro indirizzi. */
    @PostMapping("/obiettivi/aggiorna-coordinate")
    public StatoAggiornamentoCoordinateDTO avviaAggiornamentoCoordinate(HttpServletRequest httpRequest) {
        return aggiornamentoCoordinateService.avvia(AuthenticatedUser.getUtenteId(), httpRequest.getRemoteAddr());
    }

    @GetMapping("/obiettivi/aggiorna-coordinate/stato")
    public StatoAggiornamentoCoordinateDTO statoAggiornamentoCoordinate() {
        return aggiornamentoCoordinateService.stato();
    }

    /** Conferma la via e le coordinate proposte per un obiettivo con indirizzo non corrispondente. */
    @PostMapping("/obiettivi/aggiorna-coordinate/proposte/{obiettivoId}/conferma")
    public StatoAggiornamentoCoordinateDTO confermaProposta(@PathVariable Long obiettivoId, HttpServletRequest httpRequest) {
        return aggiornamentoCoordinateService.confermaProposta(obiettivoId, AuthenticatedUser.getUtenteId(),
                httpRequest.getRemoteAddr());
    }

    @PostMapping("/obiettivi/aggiorna-coordinate/proposte/{obiettivoId}/scarta")
    public StatoAggiornamentoCoordinateDTO scartaProposta(@PathVariable Long obiettivoId) {
        return aggiornamentoCoordinateService.scartaProposta(obiettivoId);
    }

    @PostMapping("/obiettivi/aggiorna-coordinate/proposte/conferma-tutte")
    public StatoAggiornamentoCoordinateDTO confermaTutteLeProposte(HttpServletRequest httpRequest) {
        return aggiornamentoCoordinateService.confermaTutte(AuthenticatedUser.getUtenteId(), httpRequest.getRemoteAddr());
    }

    @GetMapping("/pattuglie/{pattugliaId}/obiettivi")
    public PaginaDTO<ObiettivoDTO> listaObiettivi(@PathVariable Long pattugliaId,
                                                   @RequestParam(defaultValue = "0") int pagina,
                                                   @RequestParam(defaultValue = "10") int dimensione) {
        return obiettivoService.findPerAdmin(pattugliaId, pagina, dimensione);
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
