import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Obiettivo, GiornoSettimana, FasciaOraria, TipoObiettivo, ETICHETTE_TIPO_OBIETTIVO } from './pattuglia.service';

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
  attiva: boolean;
  tipoCarburante: 'BENZINA' | 'GASOLIO';
}

/** Pagina di risultati restituita dalle ricerche paginate (pagina numerata da 0). */
export interface Pagina<T> {
  contenuto: T[];
  pagina: number;
  dimensione: number;
  totaleElementi: number;
  totalePagine: number;
}

export interface PattugliaRicerca extends Pattuglia {
  /** Obiettivi il cui nome corrisponde al testo cercato. */
  obiettiviCorrispondenti: string[];
}

/** Evento del log di sistema. */
export interface LogSistema {
  id: number;
  dataOra: string;          // ISO, es. "2026-09-21T15:31:56"
  username: string | null;  // null per eventi di sistema
  nomeUtente: string | null;
  tipoEvento: string;
  descrizione: string | null;
  indirizzoIp: string | null;
}

/** Correzione di indirizzo proposta per un obiettivo, da confermare o scartare. */
export interface PropostaIndirizzo {
  obiettivoId: number;
  nome: string;
  viaAttuale: string;
  comune: string;
  viaProposta: string | null; // null = il luogo trovato non è una strada: si aggiornano solo le coordinate
  indirizzoTrovato: string;
  latitudine: number;
  longitudine: number;
}

/** Avanzamento dell'aggiornamento massivo delle coordinate degli obiettivi. */
export interface StatoAggiornamentoCoordinate {
  stato: 'MAI_ESEGUITO' | 'IN_CORSO' | 'COMPLETATO' | 'ERRORE';
  totale: number;
  elaborati: number;
  aggiornati: number;
  giaConCoordinate: number; // obiettivi che avevano già le coordinate e sono stati saltati
  errori: number;
  nonTrovati: string[];
  proposte: PropostaIndirizzo[];
  messaggio: string | null;
}

export interface NuovoObiettivo {
  nome: string;
  tipoObiettivo: TipoObiettivo;
  via: string;
  comune: string;
  latitudine: number;
  longitudine: number;
  priorita: boolean;
  /** Almeno una richiesta: un obiettivo senza fasce non sarebbe mai visibile alle pattuglie. */
  fasceOrarie: FasciaOraria[];
  telefonoRiferimento: string | null; // cellulare per l'avviso WhatsApp dopo il flag, opzionale
}

// Riesportati per comodità di chi importa solo da admin.service (uso storico nei componenti admin).
export type { GiornoSettimana, FasciaOraria, TipoObiettivo };
export { ETICHETTE_TIPO_OBIETTIVO };

/** Orari dei tre turni standard, usati come proposta rapida per le fasce orarie ("HH:mm"). Notte attraversa la mezzanotte. */
export interface ConfigurazioneTurni {
  mattinaInizio: string;
  mattinaFine: string;
  pomeriggioInizio: string;
  pomeriggioFine: string;
  notteInizio: string;
  notteFine: string; // del giorno successivo a notteInizio
}

/** Pattuglia membro di un gruppo di accorpamento. */
export interface MembroGruppo {
  id: number;
  nome: string;
  descrizione: string;
  attiva: boolean;
}

/** Gruppo di accorpamento: le pattuglie membro vedono e possono flaggare gli obiettivi le une delle altre. */
export interface GruppoPattuglie {
  id: number;
  nome: string;
  membri: MembroGruppo[];
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

  /** Pattuglie associate (preferite) a un utente, anche disattivate. */
  pattuglieDiUtente(utenteId: number): Observable<Pattuglia[]> {
    return this.http.get<Pattuglia[]>(`/api/admin/utenti/${utenteId}/pattuglie`);
  }

  associaPattuglia(utenteId: number, pattugliaId: number): Observable<void> {
    return this.http.put<void>(`/api/admin/utenti/${utenteId}/pattuglie/${pattugliaId}`, {});
  }

  rimuoviPattuglia(utenteId: number, pattugliaId: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/utenti/${utenteId}/pattuglie/${pattugliaId}`);
  }

  // ---- Pattuglie ----

  listaPattuglie(): Observable<Pattuglia[]> {
    return this.http.get<Pattuglia[]>('/api/admin/pattuglie');
  }

  cercaPattuglie(q: string, pagina: number, dimensione = 10): Observable<Pagina<PattugliaRicerca>> {
    return this.http.get<Pagina<PattugliaRicerca>>('/api/admin/pattuglie/ricerca', {
      params: { q, pagina, dimensione }
    });
  }

  creaPattuglia(
    nome: string, descrizione: string, tipoCarburante?: 'BENZINA' | 'GASOLIO'
  ): Observable<Pattuglia> {
    return this.http.post<Pattuglia>('/api/admin/pattuglie', { nome, descrizione, tipoCarburante });
  }

  impostaStatoPattuglia(id: number, attiva: boolean): Observable<Pattuglia> {
    return this.http.put<Pattuglia>(`/api/admin/pattuglie/${id}/stato`, { attiva });
  }

  // ---- Obiettivi ----

  creaObiettivo(pattugliaId: number, obiettivo: NuovoObiettivo): Observable<{ id: number }> {
    return this.http.post<{ id: number }>(`/api/admin/pattuglie/${pattugliaId}/obiettivi`, obiettivo);
  }

  listaObiettivi(pattugliaId: number, pagina: number, dimensione = 10): Observable<Pagina<Obiettivo>> {
    return this.http.get<Pagina<Obiettivo>>(`/api/admin/pattuglie/${pattugliaId}/obiettivi`, {
      params: { pagina, dimensione }
    });
  }

  aggiornaObiettivo(obiettivoId: number, obiettivo: NuovoObiettivo): Observable<{ id: number }> {
    return this.http.put<{ id: number }>(`/api/admin/obiettivi/${obiettivoId}`, obiettivo);
  }

  avviaAggiornamentoCoordinate(): Observable<StatoAggiornamentoCoordinate> {
    return this.http.post<StatoAggiornamentoCoordinate>('/api/admin/obiettivi/aggiorna-coordinate', {});
  }

  statoAggiornamentoCoordinate(): Observable<StatoAggiornamentoCoordinate> {
    return this.http.get<StatoAggiornamentoCoordinate>('/api/admin/obiettivi/aggiorna-coordinate/stato');
  }

  confermaPropostaIndirizzo(obiettivoId: number): Observable<StatoAggiornamentoCoordinate> {
    return this.http.post<StatoAggiornamentoCoordinate>(
      `/api/admin/obiettivi/aggiorna-coordinate/proposte/${obiettivoId}/conferma`, {});
  }

  scartaPropostaIndirizzo(obiettivoId: number): Observable<StatoAggiornamentoCoordinate> {
    return this.http.post<StatoAggiornamentoCoordinate>(
      `/api/admin/obiettivi/aggiorna-coordinate/proposte/${obiettivoId}/scarta`, {});
  }

  confermaTutteLeProposte(): Observable<StatoAggiornamentoCoordinate> {
    return this.http.post<StatoAggiornamentoCoordinate>(
      '/api/admin/obiettivi/aggiorna-coordinate/proposte/conferma-tutte', {});
  }

  // ---- Log di sistema ----

  /** Eventi dal più recente; dal/al in formato yyyy-MM-dd (estremi inclusi), tutti i filtri facoltativi. */
  listaLog(filtri: { dal?: string; al?: string; tipo?: string }, pagina: number, dimensione = 25): Observable<Pagina<LogSistema>> {
    const params: Record<string, string | number> = { pagina, dimensione };
    if (filtri.dal) { params['dal'] = filtri.dal; }
    if (filtri.al) { params['al'] = filtri.al; }
    if (filtri.tipo) { params['tipo'] = filtri.tipo; }
    return this.http.get<Pagina<LogSistema>>('/api/admin/log', { params });
  }

  tipiEventoLog(): Observable<string[]> {
    return this.http.get<string[]>('/api/admin/log/tipi');
  }

  // ---- Geocodifica ----

  geocodifica(indirizzo: string): Observable<GeocodificaResult> {
    return this.http.get<GeocodificaResult>('/api/admin/geocodifica', { params: { indirizzo } });
  }

  // ---- Turni predefiniti ----

  leggiTurni(): Observable<ConfigurazioneTurni> {
    return this.http.get<ConfigurazioneTurni>('/api/admin/turni');
  }

  aggiornaTurni(turni: ConfigurazioneTurni): Observable<ConfigurazioneTurni> {
    return this.http.put<ConfigurazioneTurni>('/api/admin/turni', turni);
  }

  // ---- Accorpamento pattuglie ----

  listaGruppiPattuglie(): Observable<GruppoPattuglie[]> {
    return this.http.get<GruppoPattuglie[]>('/api/admin/gruppi-pattuglie');
  }

  creaGruppoPattuglie(nome: string): Observable<GruppoPattuglie> {
    return this.http.post<GruppoPattuglie>('/api/admin/gruppi-pattuglie', { nome });
  }

  eliminaGruppoPattuglie(gruppoId: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/gruppi-pattuglie/${gruppoId}`);
  }

  aggiungiMembroGruppo(gruppoId: number, pattugliaId: number): Observable<GruppoPattuglie> {
    return this.http.put<GruppoPattuglie>(`/api/admin/gruppi-pattuglie/${gruppoId}/membri/${pattugliaId}`, {});
  }

  rimuoviMembroGruppo(gruppoId: number, pattugliaId: number): Observable<GruppoPattuglie> {
    return this.http.delete<GruppoPattuglie>(`/api/admin/gruppi-pattuglie/${gruppoId}/membri/${pattugliaId}`);
  }
}
