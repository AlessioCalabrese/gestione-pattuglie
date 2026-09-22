package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.entity.Utente;
import com.vigilanza.pattuglie.repository.UtenteRepository;
import com.vigilanza.pattuglie.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

        String token = jwtUtil.generaToken(utente.getId(), utente.getUsername(), utente.getRuolo().name(),
                utente.getNome(), utente.getCognome());
        logSistemaService.registra(utente, "LOGIN", "Login effettuato (password)", indirizzoIp);
        return token;
    }

    /**
     * Login con il codice del tag NFC, per qualunque ruolo. Il codice arriva dal lettore NFC oppure, se il
     * dispositivo non lo supporta, digitato a mano ("manuale"): in quel caso l'accesso è tracciato come tale.
     */
    public String loginNfc(String nfcTagId, String indirizzoIp, boolean manuale) {
        String codice = CodiceNfc.normalizza(nfcTagId);
        if (codice.isEmpty()) {
            throw new SecurityException("Tag NFC non riconosciuto");
        }

        List<Utente> trovati = utenteRepository.findByNfcTagIdNormalizzato(codice);
        if (trovati.size() != 1) { // nessuno, oppure codice assegnato in modo ambiguo
            throw new SecurityException("Tag NFC non riconosciuto");
        }
        Utente utente = trovati.get(0);

        if (!utente.isAbilitato()) {
            throw new SecurityException("Utente disabilitato");
        }

        String token = jwtUtil.generaToken(utente.getId(), utente.getUsername(), utente.getRuolo().name(),
                utente.getNome(), utente.getCognome());
        logSistemaService.registra(utente, "LOGIN",
                manuale ? "Login effettuato (tag NFC, codice inserito manualmente)" : "Login effettuato (tag NFC)",
                indirizzoIp);
        return token;
    }

    public void logout(String token, Utente utente, String indirizzoIp) {
        jwtUtil.revocaToken(token);
        logSistemaService.registra(utente, "LOGOUT", "Logout effettuato", indirizzoIp);
    }
}
