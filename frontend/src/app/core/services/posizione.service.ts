import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export type FontePosizione = 'GPS' | 'RETE' | 'SEDE';

export interface PosizioneRilevata {
  latitudine: number;
  longitudine: number;
  precisioneMetri: number | null;
  fonte: FontePosizione;
  localita: string | null;
}

interface PosizioneDaRete {
  latitudine: number;
  longitudine: number;
  precisioneMetri: number;
  fonte: 'RETE' | 'SEDE';
  localita: string | null;
}

/** Servizio di geolocalizzazione da IP interrogato dal browser quando il server non può farlo (senza chiave). */
const URL_GEOLOCALIZZAZIONE_IP = 'https://ipwho.is/';
/** Errore massimo assunto per una posizione stimata dall'IP (livello di città), come sul server. */
const PRECISIONE_STIMA_IP_METRI = 10000;

interface RispostaIpwho {
  success?: boolean;
  latitude?: number;
  longitude?: number;
  city?: string;
  region?: string;
}

/**
 * Rileva la posizione del dispositivo: prima dal GPS; se non è disponibile (dispositivo senza GPS, permesso
 * negato, timeout) la stima dalla connessione di rete tramite il server. La stima da rete è approssimativa
 * (livello di città): chi la usa deve tenerne conto e segnalarla.
 */
@Injectable({ providedIn: 'root' })
export class PosizioneService {

  constructor(private http: HttpClient) {}

  /** Posizione dal GPS o, in mancanza, dalla rete. Rifiuta la promessa se nessuna delle due è disponibile. */
  async rileva(): Promise<PosizioneRilevata> {
    try {
      return await this.daGps();
    } catch {
      return await this.daRete();
    }
  }

  private daGps(): Promise<PosizioneRilevata> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocalizzazione non supportata'));
        return;
      }
      navigator.geolocation.getCurrentPosition(
        posizione => resolve({
          latitudine: posizione.coords.latitude,
          longitudine: posizione.coords.longitude,
          precisioneMetri: posizione.coords.accuracy,
          fonte: 'GPS',
          localita: null
        }),
        reject,
        { enableHighAccuracy: true, timeout: 10000 }
      );
    });
  }

  /**
   * Stima dalla rete tramite il server; se il server non riesce (tipicamente perché vede solo un IP interno,
   * quando applicazione e dispositivo sono sulla stessa rete locale) la chiede direttamente dal browser, che
   * agli occhi del servizio esterno ha l'IP pubblico della connessione di riferimento.
   */
  private async daRete(): Promise<PosizioneRilevata> {
    try {
      const r = await firstValueFrom(this.http.get<PosizioneDaRete>('/api/posizione/da-rete'));
      return {
        latitudine: r.latitudine,
        longitudine: r.longitudine,
        precisioneMetri: r.precisioneMetri,
        fonte: r.fonte,
        localita: r.localita
      };
    } catch {
      return await this.daReteDalBrowser();
    }
  }

  private async daReteDalBrowser(): Promise<PosizioneRilevata> {
    const r = await firstValueFrom(this.http.get<RispostaIpwho>(URL_GEOLOCALIZZAZIONE_IP));
    if (r.success === false || r.latitude == null || r.longitude == null) {
      throw new Error('Posizione dalla rete non disponibile');
    }
    const localita = [r.city, r.region].filter(v => !!v).join(', ');
    return {
      latitudine: r.latitude,
      longitudine: r.longitude,
      precisioneMetri: PRECISIONE_STIMA_IP_METRI,
      fonte: 'RETE',
      localita: localita || null
    };
  }

  /** Testo che descrive l'origine di una posizione non GPS, da mostrare all'utente e registrare nelle note. */
  static descrizioneFonte(posizione: PosizioneRilevata): string {
    const luogo = posizione.localita ? ` (${posizione.localita})` : '';
    return posizione.fonte === 'SEDE'
      ? 'Posizione della sede di riferimento: GPS non disponibile'
      : `Posizione stimata dalla rete${luogo}: GPS non disponibile, precisione limitata`;
  }
}
