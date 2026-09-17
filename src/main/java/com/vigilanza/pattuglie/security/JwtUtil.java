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

    @Value("${jwt.secret}")
    private String secretConfigurato;

    @Value("${jwt.expiration-ms:28800000}") // default 8 ore
    private long scadenzaMs;

    // Blacklist in-memory dei token invalidati al logout.
    // In un deployment multi-istanza, sostituire con Redis o una tabella su DB.
    private final Set<String> tokenRevocati = ConcurrentHashMap.newKeySet();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretConfigurato.getBytes());
    }

    public String generaToken(Long utenteId, String username, String ruolo) {
        Date now = new Date();
        Date scadenza = new Date(now.getTime() + scadenzaMs);

        return Jwts.builder()
                .subject(username)
                .claim("utenteId", utenteId)
                .claim("ruolo", ruolo)
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
