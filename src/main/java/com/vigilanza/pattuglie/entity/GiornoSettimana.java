package com.vigilanza.pattuglie.entity;

public enum GiornoSettimana {
    LUNEDI,
    MARTEDI,
    MERCOLEDI,
    GIOVEDI,
    VENERDI,
    SABATO,
    DOMENICA;

    /** Converte il valore restituito da LocalDate.getDayOfWeek() nell'enum italiano corrispondente. */
    public static GiornoSettimana daDayOfWeek(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> LUNEDI;
            case TUESDAY -> MARTEDI;
            case WEDNESDAY -> MERCOLEDI;
            case THURSDAY -> GIOVEDI;
            case FRIDAY -> VENERDI;
            case SATURDAY -> SABATO;
            case SUNDAY -> DOMENICA;
        };
    }

    /** Il giorno della settimana successivo (DOMENICA -&gt; LUNEDI). */
    public GiornoSettimana successivo() {
        GiornoSettimana[] valori = values();
        return valori[(this.ordinal() + 1) % valori.length];
    }
}
