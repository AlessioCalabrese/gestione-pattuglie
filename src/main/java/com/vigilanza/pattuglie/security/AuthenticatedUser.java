package com.vigilanza.pattuglie.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static Long getUtenteId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getDetails() == null) {
            throw new SecurityException("Utente non autenticato");
        }
        return (Long) auth.getDetails();
    }

    public static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new SecurityException("Utente non autenticato");
        }
        return (String) auth.getPrincipal();
    }
}
