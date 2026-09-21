package com.vigilanza.pattuglie.service;

import java.util.Locale;

/**
 * Forma normalizzata del codice di un tag NFC. Il lettore fornisce il seriale come "04:a1:b2:c3:d4:e5:f6",
 * ma digitandolo a mano lo si scrive in modi diversi (maiuscole, senza due punti, con spazi o trattini):
 * per confrontarli si ignorano maiuscole/minuscole e separatori.
 */
final class CodiceNfc {

    private CodiceNfc() {
    }

    /** Codice in minuscolo e senza ':', '-' e spazi; stringa vuota se assente. */
    static String normalizza(String codice) {
        if (codice == null) {
            return "";
        }
        return codice.replaceAll("[\\s:\\-]", "").toLowerCase(Locale.ROOT);
    }
}
