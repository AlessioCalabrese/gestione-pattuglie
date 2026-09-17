import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Pattuglia {
  id: number;
  nome: string;
  descrizione: string;
  veicoloTarga: string;
  attiva: boolean;
}

export interface Obiettivo {
  id: number;
  pattugliaId: number;
  nome: string;
  indirizzo: string;
  latitudine: number;
  longitudine: number;
  ordineVisita: number | null;
  attivo: boolean;
  flaggatoOggi: boolean | null;
  ultimoFlagDataOra: string | null;
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
