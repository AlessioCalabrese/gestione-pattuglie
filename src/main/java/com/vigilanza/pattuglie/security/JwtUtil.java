package com.vigilanza.pattuglie.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    @Value("${app.security.jwt-secret}")
    private String secretConfigurato;

    @Value("${app.security.jwt-expiration-minutes:480}") // default 8 ore
    private long scadenzaMinuti;

    // Blacklist in-memory dei token invalidati al logout.
    // In un deployment multi-istanza, sostituire con Redis o una tabella su DB.
    private final Set<String> tokenRevocati = ConcurrentHashMap.newKeySet();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretConfigurato.getBytes());
    }

    /**
     * Nome e cognome sono inclusi nel token solo per comodità della UI (es. "Ciao, Nome Cognome" nella
     * schermata di selezione pattuglia), lette lato client senza bisogno di una chiamata API aggiuntiva:
     * non vanno usate per decisioni di sicurezza, che restano basate su utenteId/ruolo.
     */
    public String generaToken(Long utenteId, String username, String ruolo, String nome, String cognome) {
        Date now = new Date();
        Date scadenza = new Date(now.getTime() + scadenzaMinuti * 60_000L);

        return Jwts.builder()
                .subject(username)
                .claim("utenteId", utenteId)
                .claim("ruolo", ruolo)
                .claim("nome", nome)
                .claim("cognome", cognome)
                .issuedAt(now)
                .expiration(scadenza)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims estraiClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValido(String token) {
        if (tokenRevocati.contains(token)) {
            return false;
        }
        try {
            Claims claims = estraiClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void revocaToken(String token) {
        tokenRevocati.add(token);
    }

    public String getUsername(String token) {
        return estraiClaims(token).getSubject();
    }

    public Long getUtenteId(String token) {
        return estraiClaims(token).get("utenteId", Long.class);
    }

    public String getRuolo(String token) {
        return estraiClaims(token).get("ruolo", String.class);
    }
}
