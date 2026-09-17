import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Utente {
  id: number;
  username: string;
  nome: string;
  cognome: string;
  ruolo: 'ADMIN' | 'PATTUGLIA';
  abilitato: boolean;
  nfcTagId?: string;
}

export interface NuovoUtente {
  username: string;
  password: string;
  nome: string;
  cognome: string;
  ruolo: 'ADMIN' | 'PATTUGLIA';
  nfcTagId?: string;
}

export interface Pattuglia {
  id: number;
  nome: string;
  descrizione: string;
  veicoloTarga: string;
  attiva: boolean;
  consumoMedioL100Km: number;
  tipoCarburante: 'BENZINA' | 'GASOLIO';
}

export interface NuovoObiettivo {
  nome: string;
  indirizzo: string;
  latitudine: number;
  longitudine: number;
}

export interface GeocodificaResult {
  latitudine: number;
  longitudine: number;
  indirizzoNormalizzato: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {

  constructor(private http: HttpClient) {}

  // ---- Utenti ----

  listaUtenti(): Observable<Utente[]> {
    return this.http.get<Utente[]>('/api/admin/utenti');
  }

  creaUtente(utente: NuovoUtente): Observable<Utente> {
    return this.http.post<Utente>('/api/admin/utenti', utente);
  }

  impostaAbilitazione(id: number, abilitato: boolean): Observable<Utente> {
    return this.http.put<Utente>(`/api/admin/utenti/${id}/abilitazione`, { abilitato });
  }

  eliminaUtente(id: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/utenti/${id}`);
  }

  // ---- Pattuglie ----

  listaPattuglie(): Observable<Pattuglia[]> {
    return this.http.get<Pattuglia[]>('/api/admin/pattuglie');
  }

  creaPattuglia(
    nome: string, descrizione: string, veicoloTarga: string,
    consumoMedioL100Km?: number, tipoCarburante?: 'BENZINA' | 'GASOLIO'
  ): Observable<Pattuglia> {
    return this.http.post<Pattuglia>('/api/admin/pattuglie', {
      nome, descrizione, veicoloTarga,
      consumoMedioL100Km: consumoMedioL100Km != null ? String(consumoMedioL100Km) : undefined,
      tipoCarburante
    });
  }

  impostaStatoPattuglia(id: number, attiva: boolean): Observable<Pattuglia> {
    return this.http.put<Pattuglia>(`/api/admin/pattuglie/${id}/stato`, { attiva });
  }

  // ---- Obiettivi ----

  creaObiettivo(pattugliaId: number, obiettivo: NuovoObiettivo): Observable<{ id: number }> {
    return this.http.post<{ id: number }>(`/api/admin/pattuglie/${pattugliaId}/obiettivi`, obiettivo);
  }

  // ---- Geocodifica ----

  geocodifica(indirizzo: string): Observable<GeocodificaResult> {
    return this.http.get<GeocodificaResult>('/api/admin/geocodifica', { params: { indirizzo } });
  }
}
