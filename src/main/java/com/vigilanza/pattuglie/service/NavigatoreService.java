package com.vigilanza.pattuglie.service;

import com.vigilanza.pattuglie.dto.TrattaNavigatoreDTO;
import com.vigilanza.pattuglie.entity.Obiettivo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Trasforma il percorso ottimizzato in link di Google Maps ("Maps URLs") che configurano il navigatore
 * con le tappe nell'ordine consigliato.
 *
 * Google Maps accetta al massimo 9 tappe intermedie più la destinazione per link, quindi i percorsi più
 * lunghi vengono divisi in tratte consecutive: ciascuna parte dall'ultima tappa della precedente.
 * Non si usa l'ottimizzazione di Google (optimize:true): l'ordine è quello calcolato da noi, che tiene
 * conto ad esempio delle priorità, e Google lo segue così com'è calcolando le strade tra le tappe.
 */
@Service
public class NavigatoreService {

    /** Tappe per link: 9 intermedie + la destinazione (limite dei Maps URLs). */
    static final int MAX_TAPPE_PER_TRATTA = 10;

    private static final String BASE_URL = "https://www.google.com/maps/dir/?api=1";

    /**
     * Link di navigazione per gli obiettivi, già nell'ordine di visita. La prima tratta non indica l'origine:
     * Google Maps parte dalla posizione reale del dispositivo nel momento in cui si apre il link. Gli obiettivi
     * senza coordinate valide vengono saltati.
     */
    public List<TrattaNavigatoreDTO> tratte(List<Obiettivo> ordinati) {
        List<Obiettivo> tappe = ordinati.stream().filter(NavigatoreService::haCoordinate).toList();

        List<TrattaNavigatoreDTO> tratte = new ArrayList<>();
        for (int inizio = 0; inizio < tappe.size(); inizio += MAX_TAPPE_PER_TRATTA) {
            List<Obiettivo> tratta = tappe.subList(inizio, Math.min(inizio + MAX_TAPPE_PER_TRATTA, tappe.size()));
            Obiettivo origine = inizio == 0 ? null : tappe.get(inizio - 1);
            tratte.add(new TrattaNavigatoreDTO(tratte.size() + 1,
                    tratta.stream().map(Obiettivo::getNome).toList(),
                    url(origine, tratta)));
        }
        return tratte;
    }

    /** URL di una tratta: destinazione = ultima tappa, le altre sono tappe intermedie nell'ordine dato. */
    static String url(Obiettivo origine, List<Obiettivo> tratta) {
        Obiettivo destinazione = tratta.get(tratta.size() - 1);
        StringBuilder url = new StringBuilder(BASE_URL);
        if (origine != null) {
            url.append("&origin=").append(codifica(coordinate(origine)));
        }
        url.append("&destination=").append(codifica(coordinate(destinazione)));

        if (tratta.size() > 1) {
            StringBuilder intermedie = new StringBuilder();
            for (Obiettivo tappa : tratta.subList(0, tratta.size() - 1)) {
                if (intermedie.length() > 0) {
                    intermedie.append('|');
                }
                intermedie.append(coordinate(tappa));
            }
            url.append("&waypoints=").append(codifica(intermedie.toString()));
        }
        // dir_action=navigate: sul telefono avvia subito la navigazione invece di mostrare solo l'anteprima
        return url.append("&travelmode=driving&dir_action=navigate").toString();
    }

    private static String coordinate(Obiettivo o) {
        return o.getLatitudine().toPlainString() + "," + o.getLongitudine().toPlainString();
    }

    private static String codifica(String valore) {
        return URLEncoder.encode(valore, StandardCharsets.UTF_8);
    }

    /** Coordinate presenti e diverse da (0, 0), che è il segnaposto di un obiettivo non ancora geocodificato. */
    private static boolean haCoordinate(Obiettivo o) {
        BigDecimal lat = o.getLatitudine();
        BigDecimal lng = o.getLongitudine();
        return lat != null && lng != null && !(lat.signum() == 0 && lng.signum() == 0);
    }
}
