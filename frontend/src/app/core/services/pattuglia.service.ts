import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Pattuglia {
  id: number;
  nome: string;
  descrizione: string;
  attiva: boolean;
  preferita: boolean; // tra le preferite dell'utente loggato
}

export interface Obiettivo {
  id: number;
  pattugliaId: number;
  nome: string;
  via: string;
  comune: string;
  indirizzo: string;
  latitudine: number;
  longitudine: number;
  ordineVisita: number | null;
  attivo: boolean;
  priorita: boolean;
  giorniAttivi: string[];
  oraInizio: string | null;
  oraFine: string | null;
  ripetizioniGiornaliere: number;
  telefonoRiferimento: string | null;
  whatsappUrl: string | null; // presente solo se c'è un telefono e un flag oggi
  flaggatoOggi: boolean | null;
  ultimoFlagDataOra: string | null;
  numeroFlagOggi: number;
  completatoOggi: boolean;
  inServizioOra: boolean;
}

export interface FlagRequest {
  obiettivoId: number;
  latitudine: number;
  longitudine: number;
  precisioneMetri?: number;
  note?: string;
}

export interface RisultatoOttimizzazione {
  obiettivi: Obiettivo[];
  obiettiviInServizioOggi: number;
  distanzaOriginaleKm: number;
  distanzaOttimizzataKm: number;
  risparmioKm: number;
  risparmioPercentuale: number;
  litriStimatiOriginali: number;
  litriStimatiOttimizzati: number;
  risparmioLitri: number;
  risparmioCosto: number;
  prezzoCarburanteAlLitro: number;
}

@Injectable({ providedIn: 'root' })
export class PattugliaService {

  constructor(private http: HttpClient) {}

  /** Le preferite dell'utente se ne ha scelte (a meno di tutte=true), altrimenti tutte le pattuglie attive. */
  pattuglieSelezionabili(tutte = false): Observable<Pattuglia[]> {
    return this.http.get<Pattuglia[]>('/api/pattuglie/mie', { params: { tutte } });
  }

  impostaPreferita(pattugliaId: number, preferita: boolean): Observable<void> {
    return this.http.put<void>(`/api/pattuglie/${pattugliaId}/preferita`, { preferita });
  }

  obiettiviDiPattuglia(pattugliaId: number): Observable<Obiettivo[]> {
    return this.http.get<Obiettivo[]>(`/api/pattuglie/${pattugliaId}/obiettivi`);
  }

  flagObiettivo(pattugliaId: number, request: FlagRequest): Observable<void> {
    return this.http.post<void>(`/api/pattuglie/${pattugliaId}/obiettivi/flag`, request);
  }

  ottimizzaRotta(pattugliaId: number, lat: number, lng: number): Observable<RisultatoOttimizzazione> {
    return this.http.post<RisultatoOttimizzazione>(
      `/api/pattuglie/${pattugliaId}/obiettivi/ottimizza-rotta`,
      { latitudine: lat, longitudine: lng }
    );
  }
}
