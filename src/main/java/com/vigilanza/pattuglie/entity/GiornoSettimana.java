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
}
