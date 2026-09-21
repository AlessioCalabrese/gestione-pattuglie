import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of, catchError } from 'rxjs';
import { AdminService, Pattuglia, PattugliaRicerca, NuovoObiettivo, GiornoSettimana, StatoAggiornamentoCoordinate, PropostaIndirizzo } from '../../../core/services/admin.service';
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
    <div class="intestazione-pagina">
      <h1>Gestione Pattuglie e Obiettivi</h1>
      <button type="button" class="btn-aggiorna-coordinate" (click)="aggiornaCoordinate()"
              [disabled]="aggiornamentoInCorso">
        {{ aggiornamentoInCorso ? 'Aggiornamento in corso…' : 'Aggiorna coordinate obiettivi' }}
      </button>
    </div>

    <div class="pannello-coordinate" *ngIf="statoCoordinate && statoCoordinate.stato !== 'MAI_ESEGUITO'"
         [class.errore]="statoCoordinate.stato === 'ERRORE'">
      <ng-container *ngIf="statoCoordinate.stato === 'IN_CORSO'">
        <strong>Aggiornamento coordinate in corso:</strong>
        {{ statoCoordinate.elaborati }} / {{ statoCoordinate.totale }} obiettivi
        <progress [value]="statoCoordinate.elaborati" [max]="statoCoordinate.totale"></progress>
      </ng-container>
      <ng-container *ngIf="statoCoordinate.stato !== 'IN_CORSO'">
        <strong>{{ statoCoordinate.stato === 'ERRORE' ? 'Aggiornamento interrotto' : 'Aggiornamento coordinate completato' }}:</strong>
        {{ statoCoordinate.aggiornati }} aggiornati su {{ statoCoordinate.totale }} obiettivi senza coordinate,
        {{ statoCoordinate.proposte.length }} da confermare,
        {{ statoCoordinate.nonTrovati.length }} indirizzi non trovati, {{ statoCoordinate.errori }} errori.
        {{ statoCoordinate.giaConCoordinate }} obiettivi avevano già le coordinate e non sono stati toccati.
        <span *ngIf="statoCoordinate.messaggio">{{ statoCoordinate.messaggio }}</span>
        <button type="button" class="btn-chiudi" *ngIf="!statoCoordinate.proposte.length"
                (click)="statoCoordinate = null">Chiudi</button>
        <details *ngIf="statoCoordinate.nonTrovati.length">
          <summary>Obiettivi con indirizzo non trovato (restano senza coordinate)</summary>
          <ul>
            <li *ngFor="let n of statoCoordinate.nonTrovati">{{ n }}</li>
          </ul>
        </details>
      </ng-container>

      <div class="proposte" *ngIf="statoCoordinate.proposte.length">
        <div class="proposte-intestazione">
          <strong>{{ statoCoordinate.proposte.length }} indirizzi da confermare</strong>
          <span>La via inserita non corrisponde esattamente a quella trovata: controlla e conferma la correzione.</span>
          <button type="button" (click)="confermaTutteLeProposte()">Conferma tutte</button>
        </div>
        <div class="proposta" *ngFor="let pr of statoCoordinate.proposte">
          <div class="testo-proposta">
            <strong>{{ pr.nome }}</strong> — {{ pr.comune }}<br />
            Via inserita: <span class="via-errata">{{ pr.viaAttuale }}</span><br />
            <ng-container *ngIf="pr.viaProposta; else soloCoordinate">
              Via proposta: <span class="via-proposta">{{ pr.viaProposta }}</span>
            </ng-container>
            <ng-template #soloCoordinate>
              Nessuna via riconosciuta: si aggiornano solo le coordinate del luogo trovato.
            </ng-template>
            <br /><small>Trovato: {{ pr.indirizzoTrovato }}</small>
          </div>
          <div class="azioni-proposta">
            <button type="button" (click)="confermaProposta(pr)">Conferma</button>
            <button type="button" class="secondario" (click)="scartaProposta(pr)">Scarta</button>
          </div>
        </div>
      </div>
    </div>

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

    <div class="barra-ricerca">
      <input type="search" [(ngModel)]="ricerca" name="ricerca" (ngModelChange)="onRicercaCambiata()"
             placeholder="Cerca pattuglia per nome, descrizione o nome di un obiettivo" />
      <span class="conteggio">{{ totalePattuglie }} pattuglie trovate</span>
    </div>

    <p class="hint" *ngIf="caricato && pattuglie.length === 0">Nessuna pattuglia trovata.</p>

    <div class="lista-pattuglie">
      <div class="card-pattuglia" *ngFor="let p of pattuglie">
        <div class="intestazione" (click)="toggleEspansa(p.id)">
          <div>
            <h2>{{ p.nome }}</h2>
            <p>{{ p.descrizione }} — {{ p.tipoCarburante }}</p>
            <p class="corrispondenze" *ngIf="p.obiettiviCorrispondenti.length">
              Obiettivi trovati: {{ p.obiettiviCorrispondenti.join(', ') }}
            </p>
          </div>
          <span [class.badge-attivo]="p.attiva" [class.badge-bloccato]="!p.attiva">
            {{ p.attiva ? 'Attiva' : 'Disattivata' }}
          </span>
        </div>

        <div class="pannello" *ngIf="espansa === p.id">
          <button (click)="toggleStato(p)">
            {{ p.attiva ? 'Disattiva pattuglia' : 'Riattiva pattuglia' }}
          </button>

          <h3>Obiettivi ({{ totaleObiettivi }})</h3>
          <p class="hint" *ngIf="obiettivi.length === 0">Nessun obiettivo attivo per questa pattuglia.</p>
          <ul class="lista-obiettivi-admin" *ngIf="obiettivi.length > 0">
            <li *ngFor="let o of obiettivi"
                [class.in-modifica]="obiettivoInModifica === o.id"
                [class.trovato]="corrispondeARicerca(o)">
              <div>
                <strong>{{ o.nome }}</strong> — {{ o.indirizzo }}
                <span class="telefono" *ngIf="o.telefonoRiferimento">· WhatsApp: +{{ o.telefonoRiferimento }}</span>
              </div>
              <button type="button" (click)="modificaObiettivo(o)">Modifica</button>
            </li>
          </ul>
          <div class="paginazione" *ngIf="totalePagineObiettivi > 1">
            <button type="button" (click)="vaiAPaginaObiettivi(p.id, paginaObiettivi - 1)" [disabled]="paginaObiettivi === 0">« Precedente</button>
            <span>Pagina {{ paginaObiettivi + 1 }} di {{ totalePagineObiettivi }}</span>
            <button type="button" (click)="vaiAPaginaObiettivi(p.id, paginaObiettivi + 1)" [disabled]="paginaObiettivi >= totalePagineObiettivi - 1">Successiva »</button>
          </div>

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

    <div class="paginazione" *ngIf="totalePagine > 1">
      <button type="button" (click)="vaiAPagina(paginaPattuglie - 1)" [disabled]="paginaPattuglie === 0">« Precedente</button>
      <span>Pagina {{ paginaPattuglie + 1 }} di {{ totalePagine }}</span>
      <button type="button" (click)="vaiAPagina(paginaPattuglie + 1)" [disabled]="paginaPattuglie >= totalePagine - 1">Successiva »</button>
    </div>
  `,
  styleUrls: ['./gestione-pattuglie.component.css']
})
export class GestionePattuglieComponent implements OnInit, OnDestroy {
  statoCoordinate: StatoAggiornamentoCoordinate | null = null;
  private timerCoordinate: ReturnType<typeof setInterval> | null = null;

  pattuglie: PattugliaRicerca[] = [];
  caricato = false; // vero dopo la prima risposta del server (evita di mostrare "nessuna pattuglia" durante il caricamento)
  ricerca = '';
  paginaPattuglie = 0;
  totalePagine = 0;
  totalePattuglie = 0;

  espansa: number | null = null;
  messaggio = '';

  nuovaPattuglia: { nome: string; descrizione: string; tipoCarburante: 'BENZINA' | 'GASOLIO' } = {
    nome: '', descrizione: '', tipoCarburante: 'BENZINA'
  };

  obiettivi: Obiettivo[] = [];
  paginaObiettivi = 0;
  totalePagineObiettivi = 0;
  totaleObiettivi = 0;
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
  private ricercaSubject = new Subject<void>();

  constructor(private adminService: AdminService) {
    this.ricercaSubject.pipe(debounceTime(400)).subscribe(() => {
      this.paginaPattuglie = 0;
      this.carica();
    });

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
    // Se un aggiornamento era in corso o ha proposte ancora da confermare (es. pagina ricaricata) le si riprende.
    this.adminService.statoAggiornamentoCoordinate().subscribe(stato => {
      if (stato.stato === 'IN_CORSO' || stato.proposte.length > 0) {
        this.statoCoordinate = stato;
      }
      if (stato.stato === 'IN_CORSO') {
        this.monitoraAggiornamento();
      }
    });
  }

  ngOnDestroy(): void {
    this.fermaMonitoraggio();
  }

  get aggiornamentoInCorso(): boolean {
    return this.statoCoordinate?.stato === 'IN_CORSO';
  }

  aggiornaCoordinate(): void {
    const conferma = confirm(
      'Verranno calcolate, a partire dall\'indirizzo, le coordinate degli obiettivi attivi che ne sono privi.\n\n' +
      'Gli obiettivi che hanno già le coordinate non vengono modificati. Con molti obiettivi l\'operazione può richiedere alcuni minuti.\n\n' +
      'Vuoi continuare?');
    if (!conferma) {
      return;
    }
    this.messaggio = '';
    this.adminService.avviaAggiornamentoCoordinate().subscribe({
      next: stato => {
        this.statoCoordinate = stato;
        if (stato.stato === 'IN_CORSO') {
          this.monitoraAggiornamento();
        }
      },
      error: err => this.messaggio = err?.error?.errore ?? 'Errore durante l\'avvio dell\'aggiornamento delle coordinate.'
    });
  }

  confermaProposta(proposta: PropostaIndirizzo): void {
    this.adminService.confermaPropostaIndirizzo(proposta.obiettivoId).subscribe({
      next: stato => this.dopoConfermaProposte(stato),
      error: err => this.messaggio = err?.error?.errore ?? 'Errore durante la conferma dell\'indirizzo.'
    });
  }

  scartaProposta(proposta: PropostaIndirizzo): void {
    this.adminService.scartaPropostaIndirizzo(proposta.obiettivoId).subscribe({
      next: stato => this.statoCoordinate = stato,
      error: err => this.messaggio = err?.error?.errore ?? 'Errore durante lo scarto della proposta.'
    });
  }

  confermaTutteLeProposte(): void {
    const n = this.statoCoordinate?.proposte.length ?? 0;
    if (!confirm(`Confermare tutte le ${n} correzioni proposte? Le vie verranno sostituite con quelle trovate.`)) {
      return;
    }
    this.adminService.confermaTutteLeProposte().subscribe({
      next: stato => this.dopoConfermaProposte(stato),
      error: err => this.messaggio = err?.error?.errore ?? 'Errore durante la conferma degli indirizzi.'
    });
  }

  private dopoConfermaProposte(stato: StatoAggiornamentoCoordinate): void {
    this.statoCoordinate = stato;
    if (this.espansa !== null) {
      this.caricaObiettivi(this.espansa); // mostra vie e coordinate aggiornate
    }
  }

  /** Interroga il server ogni 2 secondi finché l'aggiornamento non termina. */
  private monitoraAggiornamento(): void {
    this.fermaMonitoraggio();
    this.timerCoordinate = setInterval(() => {
      this.adminService.statoAggiornamentoCoordinate().subscribe({
        next: stato => {
          this.statoCoordinate = stato;
          if (stato.stato !== 'IN_CORSO') {
            this.fermaMonitoraggio();
            if (this.espansa !== null) {
              this.caricaObiettivi(this.espansa); // mostra le coordinate aggiornate
            }
          }
        },
        error: () => this.fermaMonitoraggio()
      });
    }, 2000);
  }

  private fermaMonitoraggio(): void {
    if (this.timerCoordinate !== null) {
      clearInterval(this.timerCoordinate);
      this.timerCoordinate = null;
    }
  }

  private obiettivoVuoto(): NuovoObiettivo {
    return {
      nome: '', via: '', comune: '',
      latitudine: 0, longitudine: 0, priorita: false,
      giorniAttivi: [], oraInizio: null, oraFine: null, ripetizioniGiornaliere: 1,
      telefonoRiferimento: null
    };
  }

  onIndirizzoCambiato(): void {
    this.coordinateTrovate = false;
    this.erroreGeocodifica = '';
    const { via, comune } = this.nuovoObiettivo;
    const indirizzoCompleto = [via, comune].filter(v => v && v.trim()).join(' ');
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
    this.adminService.cercaPattuglie(this.ricerca.trim(), this.paginaPattuglie).subscribe(risultato => {
      // Se la pagina richiesta non esiste più (es. dopo una nuova ricerca), si torna all'ultima disponibile.
      if (risultato.contenuto.length === 0 && risultato.totalePagine > 0 && this.paginaPattuglie > 0) {
        this.paginaPattuglie = risultato.totalePagine - 1;
        this.carica();
        return;
      }
      this.pattuglie = risultato.contenuto;
      this.totalePagine = risultato.totalePagine;
      this.totalePattuglie = risultato.totaleElementi;
      this.caricato = true;
    }, err => {
      this.pattuglie = [];
      this.totalePagine = 0;
      this.totalePattuglie = 0;
      this.messaggio = err?.error?.errore ?? 'Errore durante il caricamento delle pattuglie.';
    });
  }

  onRicercaCambiata(): void {
    this.ricercaSubject.next();
  }

  vaiAPagina(pagina: number): void {
    this.paginaPattuglie = Math.max(0, Math.min(pagina, this.totalePagine - 1));
    this.carica();
  }

  toggleEspansa(id: number): void {
    this.espansa = this.espansa === id ? null : id;
    this.obiettivi = [];
    this.paginaObiettivi = 0;
    this.totalePagineObiettivi = 0;
    this.totaleObiettivi = 0;
    this.annullaModifica();
    if (this.espansa !== null) {
      this.caricaObiettivi(this.espansa);
    }
  }

  private caricaObiettivi(pattugliaId: number): void {
    this.adminService.listaObiettivi(pattugliaId, this.paginaObiettivi).subscribe(risultato => {
      this.obiettivi = risultato.contenuto;
      this.totalePagineObiettivi = risultato.totalePagine;
      this.totaleObiettivi = risultato.totaleElementi;
    });
  }

  vaiAPaginaObiettivi(pattugliaId: number, pagina: number): void {
    this.paginaObiettivi = Math.max(0, Math.min(pagina, this.totalePagineObiettivi - 1));
    this.caricaObiettivi(pattugliaId);
  }

  /** Vero se il nome dell'obiettivo contiene il testo cercato (per evidenziarlo nell'elenco). */
  corrispondeARicerca(o: Obiettivo): boolean {
    const testo = this.ricerca.trim().toLowerCase();
    return testo !== '' && o.nome.toLowerCase().includes(testo);
  }

  /** Porta i dati dell'obiettivo nel form, per modificarli. Le coordinate esistenti restano valide finché non si cambia l'indirizzo. */
  modificaObiettivo(o: Obiettivo): void {
    this.messaggio = '';
    this.obiettivoInModifica = o.id;
    this.nuovoObiettivo = {
      nome: o.nome, via: o.via, comune: o.comune,
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
        // La nuova pattuglia potrebbe non corrispondere alla ricerca in corso: si riparte dall'elenco completo.
        this.ricerca = '';
        this.paginaPattuglie = 0;
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
