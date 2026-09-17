package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import com.vigilanza.pattuglie.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LogSistemaService logSistemaService;

    public AuthService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil, LogSistemaService logSistemaService) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.logSistemaService = logSistemaService;
    }

    public String login(String username, String password, String indirizzoIp) {
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("Credenziali non valide"));

        if (!utente.isAbilitato()) {
            throw new SecurityException("Utente disabilitato");
        }

        if (!passwordEncoder.matches(password, utente.getPassword())) {
            throw new SecurityException("Credenziali non valide");
        }

        String token = jwtUtil.generaToken(utente.getId(), utente.getUsername(), utente.getRuolo().name());
        logSistemaService.registra(utente, "LOGIN", "Login effettuato (password)", indirizzoIp);
        return token;
    }

    public String loginNfc(String nfcTagId, String indirizzoIp) {
        Utente utente = utenteRepository.findByNfcTagId(nfcTagId)
                .orElseThrow(() -> new SecurityException("Tag NFC non riconosciuto"));

        if (!utente.isAbilitato()) {
            throw new SecurityException("Utente disabilitato");
        }

        String token = jwtUtil.generaToken(utente.getId(), utente.getUsername(), utente.getRuolo().name());
        logSistemaService.registra(utente, "LOGIN", "Login effettuato (tag NFC)", indirizzoIp);
        return token;
    }

    public void logout(String token, Utente utente, String indirizzoIp) {
        jwtUtil.revocaToken(token);
        logSistemaService.registra(utente, "LOGOUT", "Logout effettuato", indirizzoIp);
    }
}
