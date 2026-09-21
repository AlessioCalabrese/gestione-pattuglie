import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of, catchError } from 'rxjs';
import { AdminService, Pattuglia, NuovoObiettivo, GiornoSettimana } from '../../../core/services/admin.service';
import { Obiettivo } from '../../../core/services/pattuglia.service';

interface GiornoOpzione {
  valore: GiornoSettimana;
  etichetta: string;
}

@Component({
  selector: 'app-gestione-pattuglie',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Gestione Pattuglie e Obiettivi</h1>

    <p class="hint hint-top">
      Il prezzo del carburante usato per stimare il risparmio nell'ottimizzazione rotta è
      recuperato automaticamente dal dataset ufficiale del Ministero delle Imprese e del Made
      in Italy (MIMIT), aggiornato ogni notte — nessuna configurazione manuale necessaria.
    </p>

    <form class="form-nuovo" (ngSubmit)="creaPattuglia()">
      <input type="text" [(ngModel)]="nuovaPattuglia.nome" name="nome" placeholder="Nome pattuglia" required />
      <input type="text" [(ngModel)]="nuovaPattuglia.descrizione" name="descrizione" placeholder="Descrizione" />
      <select [(ngModel)]="nuovaPattuglia.tipoCarburante" name="tipoCarburante">
        <option value="BENZINA">Benzina</option>
        <option value="GASOLIO">Gasolio</option>
      </select>
      <button type="submit">Crea pattuglia</button>
    </form>

    <p class="messaggio" *ngIf="messaggio">{{ messaggio }}</p>

    <div class="lista-pattuglie">
      <div class="card-pattuglia" *ngFor="let p of pattuglie">
        <div class="intestazione" (click)="toggleEspansa(p.id)">
          <div>
            <h2>{{ p.nome }}</h2>
            <p>{{ p.descrizione }} — {{ p.tipoCarburante }}</p>
          </div>
          <span [class.badge-attivo]="p.attiva" [class.badge-bloccato]="!p.attiva">
            {{ p.attiva ? 'Attiva' : 'Disattivata' }}
          </span>
        </div>

        <div class="pannello" *ngIf="espansa === p.id">
          <button (click)="toggleStato(p)">
            {{ p.attiva ? 'Disattiva pattuglia' : 'Riattiva pattuglia' }}
          </button>

          <h3>Obiettivi</h3>
          <p class="hint" *ngIf="obiettivi.length === 0">Nessun obiettivo attivo per questa pattuglia.</p>
          <ul class="lista-obiettivi-admin" *ngIf="obiettivi.length > 0">
            <li *ngFor="let o of obiettivi" [class.in-modifica]="obiettivoInModifica === o.id">
              <div>
                <strong>{{ o.nome }}</strong> — {{ o.indirizzo }}
                <span class="telefono" *ngIf="o.telefonoRiferimento">· WhatsApp: +{{ o.telefonoRiferimento }}</span>
              </div>
              <button type="button" (click)="modificaObiettivo(o)">Modifica</button>
            </li>
          </ul>

          <h3>{{ obiettivoInModifica ? 'Modifica obiettivo' : 'Aggiungi obiettivo' }}</h3>
          <form class="form-obiettivo" (ngSubmit)="salvaObiettivo(p.id)">
            <input type="text" [(ngModel)]="nuovoObiettivo.nome" name="nomeObiettivo" placeholder="Nome obiettivo" required />

            <div class="riga-indirizzo">
              <input
                type="text" class="campo-via"
                [(ngModel)]="nuovoObiettivo.via" name="via"
                placeholder="Via"
                (ngModelChange)="onIndirizzoCambiato()"
                required
              />
              <input
                type="text" class="campo-civico"
                [(ngModel)]="nuovoObiettivo.numeroCivico" name="numeroCivico"
                placeholder="N."
                (ngModelChange)="onIndirizzoCambiato()"
                required
              />
              <input
                type="text" class="campo-comune"
                [(ngModel)]="nuovoObiettivo.comune" name="comune"
                placeholder="Comune"
                (ngModelChange)="onIndirizzoCambiato()"
                required
              />
            </div>

            <input type="tel" [(ngModel)]="nuovoObiettivo.telefonoRiferimento" name="telefonoRiferimento"
                   placeholder="Cellulare di riferimento per avviso WhatsApp (opzionale, es. +39 333 1234567)" />

            <label class="checkbox-priorita">
              <input type="checkbox" [(ngModel)]="nuovoObiettivo.priorita" name="priorita" />
              Priorità alta (visitato per primo nel percorso ottimizzato)
            </label>

            <div class="blocco-pianificazione">
              <p class="sottotitolo">Giorni di servizio (obbligatorio: seleziona almeno un giorno)</p>
              <div class="giorni-settimana">
                <label *ngFor="let g of giorniDisponibili" class="chip-giorno" [class.selezionato]="isGiornoSelezionato(g.valore)">
                  <input type="checkbox" [checked]="isGiornoSelezionato(g.valore)" (change)="toggleGiorno(g.valore)" />
                  {{ g.etichetta }}
                </label>
              </div>

              <p class="sottotitolo">Fascia oraria di servizio (vuota = nessun vincolo)</p>
              <div class="riga-orario">
                <input type="time" [(ngModel)]="nuovoObiettivo.oraInizio" name="oraInizio" />
                <span>—</span>
                <input type="time" [(ngModel)]="nuovoObiettivo.oraFine" name="oraFine" />
              </div>

              <p class="sottotitolo">Ripetizioni richieste nella giornata/fascia</p>
              <input type="number" min="1" [(ngModel)]="nuovoObiettivo.ripetizioniGiornaliere" name="ripetizioniGiornaliere" class="campo-ripetizioni" />
            </div>

            <button type="submit" [disabled]="!coordinateTrovate || nuovoObiettivo.giorniAttivi.length === 0">
              {{ obiettivoInModifica ? 'Salva modifiche' : 'Aggiungi obiettivo' }}
            </button>
            <button type="button" *ngIf="obiettivoInModifica" (click)="annullaModifica()">Annulla modifica</button>
            <p class="avviso-validazione" *ngIf="coordinateTrovate && nuovoObiettivo.giorniAttivi.length === 0">
              Seleziona almeno un giorno di servizio per poter salvare l'obiettivo.
            </p>
          </form>

          <p class="stato-geocodifica" *ngIf="geocodificaInCorso">Ricerca indirizzo in corso…</p>
          <p class="stato-geocodifica ok" *ngIf="!geocodificaInCorso && coordinateTrovate">
            ✓ Trovato: {{ indirizzoNormalizzato }}
            ({{ nuovoObiettivo.latitudine | number: '1.5-5' }}, {{ nuovoObiettivo.longitudine | number: '1.5-5' }})
          </p>
          <p class="stato-geocodifica errore" *ngIf="!geocodificaInCorso && erroreGeocodifica">
            {{ erroreGeocodifica }} — puoi correggere manualmente le coordinate qui sotto.
          </p>

          <details class="coordinate-manuali">
            <summary>Correggi coordinate manualmente</summary>
            <div class="riga-coordinate">
              <input type="number" step="0.0000001" [(ngModel)]="nuovoObiettivo.latitudine" name="latManuale" placeholder="Latitudine" />
              <input type="number" step="0.0000001" [(ngModel)]="nuovoObiettivo.longitudine" name="lngManuale" placeholder="Longitudine" />
            </div>
          </details>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./gestione-pattuglie.component.css']
})
export class GestionePattuglieComponent implements OnInit {
  pattuglie: Pattuglia[] = [];
  espansa: number | null = null;
  messaggio = '';

  nuovaPattuglia: { nome: string; descrizione: string; tipoCarburante: 'BENZINA' | 'GASOLIO' } = {
    nome: '', descrizione: '', tipoCarburante: 'BENZINA'
  };

  obiettivi: Obiettivo[] = [];
  obiettivoInModifica: number | null = null;

  nuovoObiettivo: NuovoObiettivo = this.obiettivoVuoto();

  giorniDisponibili: GiornoOpzione[] = [
    { valore: 'LUNEDI', etichetta: 'Lun' },
    { valore: 'MARTEDI', etichetta: 'Mar' },
    { valore: 'MERCOLEDI', etichetta: 'Mer' },
    { valore: 'GIOVEDI', etichetta: 'Gio' },
    { valore: 'VENERDI', etichetta: 'Ven' },
    { valore: 'SABATO', etichetta: 'Sab' },
    { valore: 'DOMENICA', etichetta: 'Dom' },
  ];

  geocodificaInCorso = false;
  coordinateTrovate = false;
  indirizzoNormalizzato = '';
  erroreGeocodifica = '';

  private indirizzoSubject = new Subject<string>();

  constructor(private adminService: AdminService) {
    this.indirizzoSubject.pipe(
      debounceTime(600),
      distinctUntilChanged(),
      switchMap(indirizzoCompleto => {
        if (!indirizzoCompleto || indirizzoCompleto.trim().length < 8) {
          this.coordinateTrovate = false;
          return of(null);
        }
        this.geocodificaInCorso = true;
        this.erroreGeocodifica = '';
        return this.adminService.geocodifica(indirizzoCompleto).pipe(
          catchError(() => {
            this.erroreGeocodifica = 'Indirizzo non trovato.';
            this.coordinateTrovate = false;
            return of(null);
          })
        );
      })
    ).subscribe(risultato => {
      this.geocodificaInCorso = false;
      if (risultato) {
        this.nuovoObiettivo.latitudine = risultato.latitudine;
        this.nuovoObiettivo.longitudine = risultato.longitudine;
        this.indirizzoNormalizzato = risultato.indirizzoNormalizzato;
        this.coordinateTrovate = true;
      }
    });
  }

  ngOnInit(): void {
    this.carica();
  }

  private obiettivoVuoto(): NuovoObiettivo {
    return {
      nome: '', via: '', numeroCivico: '', comune: '',
      latitudine: 0, longitudine: 0, priorita: false,
      giorniAttivi: [], oraInizio: null, oraFine: null, ripetizioniGiornaliere: 1,
      telefonoRiferimento: null
    };
  }

  onIndirizzoCambiato(): void {
    this.coordinateTrovate = false;
    this.erroreGeocodifica = '';
    const { via, numeroCivico, comune } = this.nuovoObiettivo;
    const indirizzoCompleto = [via, numeroCivico, comune].filter(v => v && v.trim()).join(' ');
    this.indirizzoSubject.next(indirizzoCompleto);
  }

  isGiornoSelezionato(giorno: GiornoSettimana): boolean {
    return this.nuovoObiettivo.giorniAttivi.includes(giorno);
  }

  toggleGiorno(giorno: GiornoSettimana): void {
    const giorni = this.nuovoObiettivo.giorniAttivi;
    const indice = giorni.indexOf(giorno);
    if (indice >= 0) {
      giorni.splice(indice, 1);
    } else {
      giorni.push(giorno);
    }
  }

  carica(): void {
    this.adminService.listaPattuglie().subscribe(p => this.pattuglie = p);
  }

  toggleEspansa(id: number): void {
    this.espansa = this.espansa === id ? null : id;
    this.obiettivi = [];
    this.annullaModifica();
    if (this.espansa !== null) {
      this.caricaObiettivi(this.espansa);
    }
  }

  private caricaObiettivi(pattugliaId: number): void {
    this.adminService.listaObiettivi(pattugliaId).subscribe(o => this.obiettivi = o);
  }

  /** Porta i dati dell'obiettivo nel form, per modificarli. Le coordinate esistenti restano valide finché non si cambia l'indirizzo. */
  modificaObiettivo(o: Obiettivo): void {
    this.messaggio = '';
    this.obiettivoInModifica = o.id;
    this.nuovoObiettivo = {
      nome: o.nome, via: o.via, numeroCivico: o.numeroCivico, comune: o.comune,
      latitudine: o.latitudine, longitudine: o.longitudine, priorita: o.priorita,
      giorniAttivi: [...o.giorniAttivi] as GiornoSettimana[],
      oraInizio: o.oraInizio ? o.oraInizio.substring(0, 5) : null,
      oraFine: o.oraFine ? o.oraFine.substring(0, 5) : null,
      ripetizioniGiornaliere: o.ripetizioniGiornaliere,
      telefonoRiferimento: o.telefonoRiferimento ? '+' + o.telefonoRiferimento : null
    };
    this.coordinateTrovate = true;
    this.geocodificaInCorso = false;
    this.erroreGeocodifica = '';
    this.indirizzoNormalizzato = o.indirizzo;
  }

  annullaModifica(): void {
    this.obiettivoInModifica = null;
    this.nuovoObiettivo = this.obiettivoVuoto();
    this.coordinateTrovate = false;
    this.geocodificaInCorso = false;
    this.erroreGeocodifica = '';
    this.indirizzoNormalizzato = '';
  }

  creaPattuglia(): void {
    this.messaggio = '';
    const { nome, descrizione, tipoCarburante } = this.nuovaPattuglia;
    this.adminService.creaPattuglia(nome, descrizione, tipoCarburante).subscribe({
      next: () => {
        this.messaggio = 'Pattuglia creata con successo.';
        this.nuovaPattuglia = { nome: '', descrizione: '', tipoCarburante: 'BENZINA' };
        this.carica();
      },
      error: () => this.messaggio = 'Errore durante la creazione della pattuglia.'
    });
  }

  toggleStato(pattuglia: Pattuglia): void {
    this.adminService.impostaStatoPattuglia(pattuglia.id, !pattuglia.attiva).subscribe(() => this.carica());
  }

  salvaObiettivo(pattugliaId: number): void {
    this.messaggio = '';
    const inModifica = this.obiettivoInModifica !== null;
    const richiesta = inModifica
      ? this.adminService.aggiornaObiettivo(this.obiettivoInModifica!, this.nuovoObiettivo)
      : this.adminService.creaObiettivo(pattugliaId, this.nuovoObiettivo);

    richiesta.subscribe({
      next: () => {
        this.messaggio = inModifica ? 'Obiettivo aggiornato con successo.' : 'Obiettivo aggiunto con successo.';
        this.annullaModifica();
        this.caricaObiettivi(pattugliaId);
      },
      error: (err) => this.messaggio = err?.error?.errore
        ?? (inModifica ? 'Errore durante l\'aggiornamento dell\'obiettivo.' : 'Errore durante la creazione dell\'obiettivo.')
    });
  }
}
