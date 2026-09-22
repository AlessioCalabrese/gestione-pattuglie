import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Pattuglia {
  id: number;
  nome: string;
  descrizione: string;
  attiva: boolean;
  preferita: boolean; // tra le preferite dell'utente loggato
  /** Gruppo di accorpamento a cui appartiene, se configurato dall'amministratore; null = nessun accorpamento. */
  gruppoId: number | null;
  gruppoNome: string | null;
}

export type GiornoSettimana = 'LUNEDI' | 'MARTEDI' | 'MERCOLEDI' | 'GIOVEDI' | 'VENERDI' | 'SABATO' | 'DOMENICA';

export type TipoObiettivo = 'DATIX' | 'ISPEZIONE' | 'BIGLIETTAZIONE';

export const ETICHETTE_TIPO_OBIETTIVO: Record<TipoObiettivo, string> = {
  DATIX: 'Datix',
  ISPEZIONE: 'Ispezione',
  BIGLIETTAZIONE: 'Bigliettazione'
};

/**
 * Fascia oraria di servizio configurata su un obiettivo: un giorno può averne più di una (es. mattina e
 * sera) e giorni diversi possono avere fasce diverse. oraInizio/oraFine null = nessun vincolo da
 * quel lato (dall'inizio/fino alla fine della giornata).
 */
export interface FasciaOraria {
  giorno: GiornoSettimana;
  oraInizio: string | null; // "HH:mm:ss" in lettura dal server, "HH:mm" quando scritta dal form
  oraFine: string | null;
  ripetizioniRichieste: number;
}

/** Stato di una fascia oraria di oggi: quanti flag mancano e se è quella in corso adesso. */
export interface FasciaOggi {
  oraInizio: string | null;
  oraFine: string | null;
  ripetizioniRichieste: number;
  numeroFlag: number;
  completata: boolean;
  inCorsoOra: boolean;
  /** Vero se è la coda di un turno notturno iniziato ieri (es. 22:00–06:00), non un turno che inizia oggi. */
  continuaDaIeri: boolean;
}

export interface Obiettivo {
  id: number;
  pattugliaId: number;
  /** Nome della pattuglia proprietaria; utile quando la lista mostra anche obiettivi di pattuglie accorpate. */
  pattugliaNome: string;
  nome: string;
  tipoObiettivo: TipoObiettivo;
  via: string;
  comune: string;
  indirizzo: string;
  latitudine: number;
  longitudine: number;
  ordineVisita: number | null;
  attivo: boolean;
  priorita: boolean;
  /** Tutte le fasce orarie configurate (qualunque giorno). */
  fasceOrarie: FasciaOraria[];
  /** Fasce orarie di oggi, con lo stato dei flag; vuoto se oggi non è un giorno di servizio. */
  fasceOggi: FasciaOggi[];
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

/** Tratta del percorso consigliato, apribile in Google Maps (al massimo 10 tappe per link). */
export interface TrattaNavigatore {
  numero: number;
  tappe: string[]; // nomi degli obiettivi, nell'ordine di visita
  url: string;
}

export interface RisultatoOttimizzazione {
  obiettivi: Obiettivo[];
  tratteNavigatore: TrattaNavigatore[];
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
