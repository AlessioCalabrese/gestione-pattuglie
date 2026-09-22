import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of, catchError } from 'rxjs';
import {
  AdminService, Pattuglia, PattugliaRicerca, NuovoObiettivo, GiornoSettimana, FasciaOraria,
  StatoAggiornamentoCoordinate, PropostaIndirizzo, ConfigurazioneTurni, GruppoPattuglie, MembroGruppo
} from '../../../core/services/admin.service';
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
      <button type="button" class="btn-secondario" (click)="toggleTurni()">
        {{ turniEspansi ? 'Chiudi turni predefiniti' : 'Turni predefiniti' }}
      </button>
      <button type="button" class="btn-secondario" (click)="toggleGruppi()">
        {{ gruppiEspansi ? 'Chiudi accorpamento pattuglie' : 'Accorpamento pattuglie' }}
      </button>
      <button type="button" class="btn-aggiorna-coordinate" (click)="aggiornaCoordinate()"
              [disabled]="aggiornamentoInCorso">
        {{ aggiornamentoInCorso ? 'Aggiornamento in corso…' : 'Aggiorna coordinate obiettivi' }}
      </button>
    </div>

    <div class="pannello-turni" *ngIf="turniEspansi">
      <h3>Turni predefiniti</h3>
      <p class="hint">
        Orari standard proposti come punto di partenza quando configuri le fasce orarie di un obiettivo
        (restano comunque modificabili fascia per fascia). La Notte attraversa la mezzanotte: l'ora di
        fine è del giorno successivo a quella di inizio.
      </p>
      <form class="form-turni" (ngSubmit)="salvaTurni()" *ngIf="turni as t">
        <div class="turno-riga">
          <strong>Mattina</strong>
          <input type="time" [(ngModel)]="t.mattinaInizio" name="mattinaInizio" required />
          <span>—</span>
          <input type="time" [(ngModel)]="t.mattinaFine" name="mattinaFine" required />
        </div>
        <div class="turno-riga">
          <strong>Pomeriggio</strong>
          <input type="time" [(ngModel)]="t.pomeriggioInizio" name="pomeriggioInizio" required />
          <span>—</span>
          <input type="time" [(ngModel)]="t.pomeriggioFine" name="pomeriggioFine" required />
        </div>
        <div class="turno-riga">
          <strong>Notte</strong>
          <input type="time" [(ngModel)]="t.notteInizio" name="notteInizio" required />
          <span>— (giorno dopo)</span>
          <input type="time" [(ngModel)]="t.notteFine" name="notteFine" required />
        </div>
        <button type="submit">Salva turni</button>
        <p class="messaggio errore" *ngIf="messaggioTurni">{{ messaggioTurni }}</p>
      </form>
    </div>

    <div class="pannello-gruppi" *ngIf="gruppiEspansi">
      <h3>Accorpamento pattuglie</h3>
      <p class="hint">
        Le pattuglie di uno stesso gruppo vedono e possono flaggare anche gli obiettivi delle altre
        pattuglie del gruppo. Una pattuglia può appartenere al massimo a un gruppo.
      </p>
      <form class="form-nuovo-gruppo" (ngSubmit)="creaGruppo()">
        <input type="text" [(ngModel)]="nomeNuovoGruppo" name="nomeNuovoGruppo"
               placeholder="Nome del nuovo gruppo (es. Zona Nord unificata)" required />
        <button type="submit">Crea gruppo</button>
      </form>
      <p class="messaggio errore" *ngIf="messaggioGruppi">{{ messaggioGruppi }}</p>

      <div class="gruppo" *ngFor="let g of gruppiPattuglie">
        <div class="gruppo-intestazione" (click)="toggleGruppoEspanso(g.id)">
          <strong>{{ g.nome }}</strong>
          <span class="hint">{{ g.membri.length }} pattuglie</span>
          <button type="button" class="btn-elimina" (click)="eliminaGruppo(g, $event)">Elimina gruppo</button>
        </div>
        <div class="gruppo-corpo" *ngIf="gruppoEspanso === g.id">
          <ul class="lista-associate" *ngIf="g.membri.length > 0">
            <li *ngFor="let m of g.membri">
              <span>{{ m.nome }} <small>{{ m.descrizione }}</small><em *ngIf="!m.attiva"> — disattivata</em></span>
              <button type="button" class="btn-elimina" (click)="rimuoviDaGruppo(g, m)">Rimuovi</button>
            </li>
          </ul>
          <p class="hint" *ngIf="g.membri.length === 0">Nessuna pattuglia in questo gruppo.</p>

          <div class="ricerca-pattuglie">
            <input type="search" [(ngModel)]="ricercaGruppoPattuglia" [name]="'ricercaGruppo' + g.id"
                   placeholder="Cerca pattuglia da aggiungere" (keyup.enter)="cercaPattuglieGruppo()" />
            <button type="button" (click)="cercaPattuglieGruppo()">Cerca</button>
          </div>
          <ul class="lista-associate" *ngIf="risultatiGruppo.length > 0">
            <li *ngFor="let p of risultatiGruppo">
              <span>{{ p.nome }} <small>{{ p.descrizione }}</small></span>
              <button type="button" [disabled]="giaNelGruppo(g, p.id)" (click)="aggiungiAGruppo(g, p)">
                {{ giaNelGruppo(g, p.id) ? 'Già presente' : 'Aggiungi' }}
              </button>
            </li>
          </ul>
          <p class="hint" *ngIf="cercatoGruppo && risultatiGruppo.length === 0">Nessuna pattuglia trovata.</p>
        </div>
      </div>
      <p class="hint" *ngIf="gruppiPattuglie.length === 0">Nessun gruppo configurato.</p>
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
                <span class="badge-tipo">{{ o.tipoObiettivo === 'BIGLIETTAZIONE' ? 'Bigliettazione' : 'Ispezione' }}</span>
                <span class="telefono" *ngIf="o.telefonoRiferimento">· WhatsApp: +{{ o.telefonoRiferimento }}</span>
                <div class="fasce-riepilogo">{{ formattaFasceRiepilogo(o.fasceOrarie) }}</div>
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

            <select [(ngModel)]="nuovoObiettivo.tipoObiettivo" name="tipoObiettivo">
              <option value="ISPEZIONE">Ispezione</option>
              <option value="BIGLIETTAZIONE">Bigliettazione</option>
            </select>

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
              <p class="sottotitolo">
                Fasce orarie di servizio (obbligatorio: aggiungi almeno una fascia — puoi averne più
                di una nello stesso giorno, es. mattina e sera, e giorni diversi possono avere fasce diverse)
              </p>
              <div class="fascia-riga" *ngFor="let f of nuovoObiettivo.fasceOrarie; let i = index">
                <select [(ngModel)]="f.giorno" [name]="'fasciaGiorno' + i">
                  <option *ngFor="let g of giorniDisponibili" [value]="g.valore">{{ g.etichetta }}</option>
                </select>
                <select (change)="applicaTurno(f, $any($event.target).value)" title="Precompila con un turno standard">
                  <option value="">Personalizza…</option>
                  <option value="MATTINA">Turno Mattina</option>
                  <option value="POMERIGGIO">Turno Pomeriggio</option>
                  <option value="NOTTE">Turno Notte</option>
                </select>
                <input type="time" [(ngModel)]="f.oraInizio" [name]="'fasciaInizio' + i" title="Ora inizio (vuota = da mezzanotte)" />
                <span>—</span>
                <input type="time" [(ngModel)]="f.oraFine" [name]="'fasciaFine' + i" title="Ora fine (vuota = fino a mezzanotte; se precede l'inizio, la fascia è notturna e finisce il giorno dopo)" />
                <input type="number" min="1" [(ngModel)]="f.ripetizioniRichieste" [name]="'fasciaRipetizioni' + i"
                       class="campo-ripetizioni" title="Ripetizioni richieste in questa fascia" />
                <button type="button" class="btn-rimuovi-fascia" (click)="rimuoviFascia(i)" title="Rimuovi fascia">✕</button>
              </div>
              <button type="button" class="btn-aggiungi-fascia" (click)="aggiungiFascia()">+ Aggiungi fascia oraria</button>
            </div>

            <button type="submit" [disabled]="!coordinateTrovate || nuovoObiettivo.fasceOrarie.length === 0">
              {{ obiettivoInModifica ? 'Salva modifiche' : 'Aggiungi obiettivo' }}
            </button>
            <button type="button" *ngIf="obiettivoInModifica" (click)="annullaModifica()">Annulla modifica</button>
            <p class="avviso-validazione" *ngIf="coordinateTrovate && nuovoObiettivo.fasceOrarie.length === 0">
              Aggiungi almeno una fascia oraria di servizio per poter salvare l'obiettivo.
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

  // Turni predefiniti
  turniEspansi = false;
  turni: ConfigurazioneTurni | null = null;
  messaggioTurni = '';

  // Accorpamento pattuglie
  gruppiEspansi = false;
  gruppiPattuglie: GruppoPattuglie[] = [];
  messaggioGruppi = '';
  nomeNuovoGruppo = '';
  gruppoEspanso: number | null = null;
  ricercaGruppoPattuglia = '';
  risultatiGruppo: PattugliaRicerca[] = [];
  cercatoGruppo = false;

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
    this.caricaTurni(); // servono anche per il precompilamento rapido delle fasce, senza aprire il pannello turni
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
      nome: '', tipoObiettivo: 'ISPEZIONE', via: '', comune: '',
      latitudine: 0, longitudine: 0, priorita: false,
      fasceOrarie: [],
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

  aggiungiFascia(): void {
    this.nuovoObiettivo.fasceOrarie.push({ giorno: 'LUNEDI', oraInizio: null, oraFine: null, ripetizioniRichieste: 1 });
  }

  rimuoviFascia(indice: number): void {
    this.nuovoObiettivo.fasceOrarie.splice(indice, 1);
  }

  /** Precompila ora inizio/fine di una fascia con l'orario del turno standard scelto (restano poi modificabili). */
  applicaTurno(fascia: FasciaOraria, turno: string): void {
    if (!this.turni) {
      return;
    }
    if (turno === 'MATTINA') {
      fascia.oraInizio = this.turni.mattinaInizio.substring(0, 5);
      fascia.oraFine = this.turni.mattinaFine.substring(0, 5);
    } else if (turno === 'POMERIGGIO') {
      fascia.oraInizio = this.turni.pomeriggioInizio.substring(0, 5);
      fascia.oraFine = this.turni.pomeriggioFine.substring(0, 5);
    } else if (turno === 'NOTTE') {
      fascia.oraInizio = this.turni.notteInizio.substring(0, 5);
      fascia.oraFine = this.turni.notteFine.substring(0, 5);
    }
  }

  // ---- Turni predefiniti ----

  toggleTurni(): void {
    this.turniEspansi = !this.turniEspansi;
    if (this.turniEspansi && !this.turni) {
      this.caricaTurni();
    }
  }

  private caricaTurni(): void {
    this.adminService.leggiTurni().subscribe({
      next: t => this.turni = t,
      error: err => this.messaggioTurni = err?.error?.errore ?? 'Errore nel caricamento dei turni.'
    });
  }

  salvaTurni(): void {
    if (!this.turni) {
      return;
    }
    this.messaggioTurni = '';
    this.adminService.aggiornaTurni(this.turni).subscribe({
      next: t => {
        this.turni = t;
        this.messaggioTurni = 'Turni aggiornati con successo.';
      },
      error: err => this.messaggioTurni = err?.error?.errore ?? 'Errore durante il salvataggio dei turni.'
    });
  }

  // ---- Accorpamento pattuglie ----

  toggleGruppi(): void {
    this.gruppiEspansi = !this.gruppiEspansi;
    if (this.gruppiEspansi) {
      this.caricaGruppi();
    }
  }

  private caricaGruppi(): void {
    this.adminService.listaGruppiPattuglie().subscribe({
      next: g => this.gruppiPattuglie = g,
      error: err => this.messaggioGruppi = err?.error?.errore ?? 'Errore nel caricamento dei gruppi.'
    });
  }

  creaGruppo(): void {
    this.messaggioGruppi = '';
    this.adminService.creaGruppoPattuglie(this.nomeNuovoGruppo.trim()).subscribe({
      next: () => {
        this.nomeNuovoGruppo = '';
        this.caricaGruppi();
      },
      error: err => this.messaggioGruppi = err?.error?.errore ?? 'Errore durante la creazione del gruppo.'
    });
  }

  eliminaGruppo(gruppo: GruppoPattuglie, evento: Event): void {
    evento.stopPropagation();
    if (!confirm(`Eliminare il gruppo "${gruppo.nome}"? Le pattuglie tornano indipendenti, i loro obiettivi restano invariati.`)) {
      return;
    }
    this.messaggioGruppi = '';
    this.adminService.eliminaGruppoPattuglie(gruppo.id).subscribe({
      next: () => {
        if (this.gruppoEspanso === gruppo.id) {
          this.gruppoEspanso = null;
        }
        this.caricaGruppi();
      },
      error: err => this.messaggioGruppi = err?.error?.errore ?? 'Errore durante l\'eliminazione del gruppo.'
    });
  }

  toggleGruppoEspanso(gruppoId: number): void {
    this.gruppoEspanso = this.gruppoEspanso === gruppoId ? null : gruppoId;
    this.ricercaGruppoPattuglia = '';
    this.risultatiGruppo = [];
    this.cercatoGruppo = false;
  }

  giaNelGruppo(gruppo: GruppoPattuglie, pattugliaId: number): boolean {
    return gruppo.membri.some(m => m.id === pattugliaId);
  }

  cercaPattuglieGruppo(): void {
    this.adminService.cercaPattuglie(this.ricercaGruppoPattuglia.trim(), 0).subscribe({
      next: r => {
        this.risultatiGruppo = r.contenuto;
        this.cercatoGruppo = true;
      },
      error: err => this.messaggioGruppi = err?.error?.errore ?? 'Errore nella ricerca delle pattuglie.'
    });
  }

  aggiungiAGruppo(gruppo: GruppoPattuglie, pattuglia: PattugliaRicerca | MembroGruppo): void {
    this.messaggioGruppi = '';
    this.adminService.aggiungiMembroGruppo(gruppo.id, pattuglia.id).subscribe({
      next: () => this.caricaGruppi(),
      error: err => this.messaggioGruppi = err?.error?.errore ?? 'Errore durante l\'aggiunta al gruppo.'
    });
  }

  rimuoviDaGruppo(gruppo: GruppoPattuglie, pattuglia: MembroGruppo): void {
    this.messaggioGruppi = '';
    this.adminService.rimuoviMembroGruppo(gruppo.id, pattuglia.id).subscribe({
      next: () => this.caricaGruppi(),
      error: err => this.messaggioGruppi = err?.error?.errore ?? 'Errore durante la rimozione dal gruppo.'
    });
  }

  private readonly abbreviazioniGiorni: Record<string, string> = {
    LUNEDI: 'Lun', MARTEDI: 'Mar', MERCOLEDI: 'Mer', GIOVEDI: 'Gio',
    VENERDI: 'Ven', SABATO: 'Sab', DOMENICA: 'Dom'
  };

  /** Riepilogo leggibile delle fasce configurate, per l'elenco obiettivi (es. "Lun 08:00–12:00 (x1), Mar tutto il giorno (x2)"). */
  formattaFasceRiepilogo(fasce: FasciaOraria[]): string {
    if (!fasce || fasce.length === 0) {
      return 'Nessuna fascia oraria configurata';
    }
    return fasce.map(f => {
      const giorno = this.abbreviazioniGiorni[f.giorno] ?? f.giorno;
      const orario = f.oraInizio || f.oraFine
        ? `${(f.oraInizio ?? '00:00').substring(0, 5)}–${(f.oraFine ?? '24:00').substring(0, 5)}`
        : 'tutto il giorno';
      return `${giorno} ${orario} (x${f.ripetizioniRichieste})`;
    }).join(', ');
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
      nome: o.nome, tipoObiettivo: o.tipoObiettivo, via: o.via, comune: o.comune,
      latitudine: o.latitudine, longitudine: o.longitudine, priorita: o.priorita,
      // Copia profonda: modificare il form non deve toccare l'obiettivo originale finché non si salva.
      fasceOrarie: o.fasceOrarie.map(f => ({
        giorno: f.giorno,
        oraInizio: f.oraInizio ? f.oraInizio.substring(0, 5) : null,
        oraFine: f.oraFine ? f.oraFine.substring(0, 5) : null,
        ripetizioniRichieste: f.ripetizioniRichieste
      })),
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
