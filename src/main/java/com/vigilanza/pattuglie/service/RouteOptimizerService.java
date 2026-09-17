package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.entity.Obiettivo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Calcola l'ordine di visita ottimale degli obiettivi di una pattuglia,
 * minimizzando la distanza totale percorsa (approccio euristico offline,
 * senza dipendenze esterne).
 *
 * Algoritmo:
 * 1. Nearest Neighbor: costruisce una rotta di partenza scegliendo sempre
 *    il punto non ancora visitato più vicino a quello corrente.
 * 2. 2-opt: raffina la rotta scambiando coppie di archi finché non si trova
 *    più un miglioramento, eliminando gli "incroci" tipici del nearest neighbor.
 *
 * NOTA: la distanza è calcolata in linea d'aria (Haversine). Per un risultato
 * più realistico (che tenga conto di strade, sensi unici, traffico) sostituire
 * il metodo distanza(...) con una chiamata a un motore di routing stradale
 * (es. OSRM self-hosted, o un servizio equivalente), mantenendo invariata
 * la logica di ottimizzazione.
 */
@Service
public class RouteOptimizerService {

    private static final double RAGGIO_TERRA_KM = 6371.0;

    /**
     * Restituisce gli obiettivi riordinati secondo il percorso ottimizzato,
     * a partire dal punto di partenza indicato (es. sede/posizione attuale pattuglia).
     */
    public List<Obiettivo> ottimizzaRotta(double latPartenza, double lngPartenza,
                                           List<Obiettivo> obiettivi) {
        if (obiettivi == null || obiettivi.size() <= 1) {
            return obiettivi == null ? new ArrayList<>() : new ArrayList<>(obiettivi);
        }

        List<Obiettivo> rotta = nearestNeighbor(latPartenza, lngPartenza, obiettivi);
        rotta = duePuntoOpt(latPartenza, lngPartenza, rotta);

        for (int i = 0; i < rotta.size(); i++) {
            rotta.get(i).setOrdineVisita(i + 1);
        }
        return rotta;
    }

    /** Calcola la lunghezza totale (km) di una rotta data, nell'ordine fornito. */
    public double calcolaLunghezzaKm(double latPartenza, double lngPartenza, List<Obiettivo> rotta) {
        return lunghezzaTotale(latPartenza, lngPartenza, rotta);
    }

    private List<Obiettivo> nearestNeighbor(double latPartenza, double lngPartenza,
                                             List<Obiettivo> obiettivi) {
        List<Obiettivo> daVisitare = new ArrayList<>(obiettivi);
        List<Obiettivo> rotta = new ArrayList<>();

        double latCorrente = latPartenza;
        double lngCorrente = lngPartenza;

        while (!daVisitare.isEmpty()) {
            Obiettivo piuVicino = null;
            double distanzaMinima = Double.MAX_VALUE;

            for (Obiettivo o : daVisitare) {
                double d = distanza(latCorrente, lngCorrente,
                        o.getLatitudine().doubleValue(), o.getLongitudine().doubleValue());
                if (d < distanzaMinima) {
                    distanzaMinima = d;
                    piuVicino = o;
                }
            }

            rotta.add(piuVicino);
            daVisitare.remove(piuVicino);
            latCorrente = piuVicino.getLatitudine().doubleValue();
            lngCorrente = piuVicino.getLongitudine().doubleValue();
        }

        return rotta;
    }

    private List<Obiettivo> duePuntoOpt(double latPartenza, double lngPartenza,
                                         List<Obiettivo> rottaIniziale) {
        List<Obiettivo> rotta = new ArrayList<>(rottaIniziale);
        boolean miglioramentoTrovato = true;

        while (miglioramentoTrovato) {
            miglioramentoTrovato = false;
            double distanzaAttuale = lunghezzaTotale(latPartenza, lngPartenza, rotta);

            for (int i = 0; i < rotta.size() - 1; i++) {
                for (int j = i + 1; j < rotta.size(); j++) {
                    List<Obiettivo> candidata = scambia2Opt(rotta, i, j);
                    double distanzaCandidata = lunghezzaTotale(latPartenza, lngPartenza, candidata);

                    if (distanzaCandidata < distanzaAttuale) {
                        rotta = candidata;
                        distanzaAttuale = distanzaCandidata;
                        miglioramentoTrovato = true;
                    }
                }
            }
        }

        return rotta;
    }

    private List<Obiettivo> scambia2Opt(List<Obiettivo> rotta, int i, int j) {
        List<Obiettivo> nuovaRotta = new ArrayList<>(rotta.subList(0, i));
        List<Obiettivo> segmentoInvertito = new ArrayList<>(rotta.subList(i, j + 1));
        java.util.Collections.reverse(segmentoInvertito);
        nuovaRotta.addAll(segmentoInvertito);
        nuovaRotta.addAll(rotta.subList(j + 1, rotta.size()));
        return nuovaRotta;
    }

    private double lunghezzaTotale(double latPartenza, double lngPartenza, List<Obiettivo> rotta) {
        double totale = 0;
        double latCorrente = latPartenza;
        double lngCorrente = lngPartenza;

        for (Obiettivo o : rotta) {
            totale += distanza(latCorrente, lngCorrente,
                    o.getLatitudine().doubleValue(), o.getLongitudine().doubleValue());
            latCorrente = o.getLatitudine().doubleValue();
            lngCorrente = o.getLongitudine().doubleValue();
        }
        return totale;
    }

    /** Distanza in km tra due coordinate (formula di Haversine). */
    private double distanza(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAGGIO_TERRA_KM * c;
    }
}
