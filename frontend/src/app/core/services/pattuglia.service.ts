import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Pattuglia {
  id: number;
  nome: string;
  descrizione: string;
  attiva: boolean;
}

export interface Obiettivo {
  id: number;
  pattugliaId: number;
  nome: string;
  via: string;
  numeroCivico: string;
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

  pattuglieSelezionabili(): Observable<Pattuglia[]> {
    return this.http.get<Pattuglia[]>('/api/pattuglie/mie');
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
