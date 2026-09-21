package com.vigilanza.pattuglie.controller;

import com.vigilanza.pattuglie.dto.LoginRequest;
import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import com.vigilanza.pattuglie.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UtenteRepository utenteRepository;
    private final com.vigilanza.pattuglie.security.JwtUtil jwtUtil;

    public AuthController(AuthService authService, UtenteRepository utenteRepository,
                           com.vigilanza.pattuglie.security.JwtUtil jwtUtil) {
        this.authService = authService;
        this.utenteRepository = utenteRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword(),
                    httpRequest.getRemoteAddr());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("errore", e.getMessage()));
        }
    }

    @PostMapping("/login-nfc")
    public ResponseEntity<?> loginNfc(@RequestBody com.vigilanza.pattuglie.dto.LoginNfcRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            String token = authService.loginNfc(request.getNfcTagId(), httpRequest.getRemoteAddr(), request.isManuale());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("errore", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader,
                                     HttpServletRequest httpRequest) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsername(token);
        Utente utente = utenteRepository.findByUsername(username).orElse(null);

        authService.logout(token, utente, httpRequest.getRemoteAddr());
        return ResponseEntity.ok().build();
    }
}
