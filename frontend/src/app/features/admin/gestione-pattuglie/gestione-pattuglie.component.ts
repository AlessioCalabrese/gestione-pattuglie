import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of, catchError } from 'rxjs';
import { AdminService, Pattuglia, NuovoObiettivo } from '../../../core/services/admin.service';

@Component({
  selector: 'app-gestione-pattuglie',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Gestione Pattuglie e Obiettivi</h1>

    <p class="hint hint-top">
      Il prezzo del carburante usato per stimare il risparmio nell'ottimizzazione rotta è
      recuperato automaticamente dal dataset ufficiale del Ministero delle Imprese e del Made
      in Italy (MIMIT), aggiornato quotidianamente — nessuna configurazione manuale necessaria.
    </p>

    <form class="form-nuovo" (ngSubmit)="creaPattuglia()">
      <input type="text" [(ngModel)]="nuovaPattuglia.nome" name="nome" placeholder="Nome pattuglia" required />
      <input type="text" [(ngModel)]="nuovaPattuglia.descrizione" name="descrizione" placeholder="Descrizione" />
      <input type="text" [(ngModel)]="nuovaPattuglia.veicoloTarga" name="veicoloTarga" placeholder="Targa veicolo" />
      <select [(ngModel)]="nuovaPattuglia.tipoCarburante" name="tipoCarburante">
        <option value="BENZINA">Benzina</option>
        <option value="GASOLIO">Gasolio</option>
      </select>
      <input type="number" step="0.1" [(ngModel)]="nuovaPattuglia.consumoMedioL100Km" name="consumoMedioL100Km" placeholder="Consumo medio (L/100km)" />
      <button type="submit">Crea pattuglia</button>
    </form>

    <p class="messaggio" *ngIf="messaggio">{{ messaggio }}</p>

    <div class="lista-pattuglie">
      <div class="card-pattuglia" *ngFor="let p of pattuglie">
        <div class="intestazione" (click)="toggleEspansa(p.id)">
          <div>
            <h2>{{ p.nome }}</h2>
            <p>{{ p.descrizione }} — {{ p.veicoloTarga }} — {{ p.tipoCarburante }} — {{ p.consumoMedioL100Km }} L/100km</p>
          </div>
          <span [class.badge-attivo]="p.attiva" [class.badge-bloccato]="!p.attiva">
            {{ p.attiva ? 'Attiva' : 'Disattivata' }}
          </span>
        </div>

        <div class="pannello" *ngIf="espansa === p.id">
          <button (click)="toggleStato(p)">
            {{ p.attiva ? 'Disattiva pattuglia' : 'Riattiva pattuglia' }}
          </button>

          <h3>Aggiungi obiettivo</h3>
          <form class="form-obiettivo" (ngSubmit)="creaObiettivo(p.id)">
            <input type="text" [(ngModel)]="nuovoObiettivo.nome" name="nomeObiettivo" placeholder="Nome obiettivo" required />
            <input
              type="text"
              [(ngModel)]="nuovoObiettivo.indirizzo"
              name="indirizzo"
              placeholder="Indirizzo (es. Via Roma 10, Milano)"
              (ngModelChange)="onIndirizzoCambiato($event)"
              required
            />
            <button type="submit" [disabled]="!coordinateTrovate">Aggiungi obiettivo</button>
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

  nuovaPattuglia: { nome: string; descrizione: string; veicoloTarga: string; consumoMedioL100Km: number; tipoCarburante: 'BENZINA' | 'GASOLIO' } = {
    nome: '', descrizione: '', veicoloTarga: '', consumoMedioL100Km: 10, tipoCarburante: 'BENZINA'
  };
  nuovoObiettivo: NuovoObiettivo = { nome: '', indirizzo: '', latitudine: 0, longitudine: 0 };

  geocodificaInCorso = false;
  coordinateTrovate = false;
  indirizzoNormalizzato = '';
  erroreGeocodifica = '';

  private indirizzoSubject = new Subject<string>();

  constructor(private adminService: AdminService) {
    this.indirizzoSubject.pipe(
      debounceTime(600),
      distinctUntilChanged(),
      switchMap(indirizzo => {
        if (!indirizzo || indirizzo.trim().length < 5) {
          this.coordinateTrovate = false;
          return of(null);
        }
        this.geocodificaInCorso = true;
        this.erroreGeocodifica = '';
        return this.adminService.geocodifica(indirizzo).pipe(
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

  onIndirizzoCambiato(indirizzo: string): void {
    this.coordinateTrovate = false;
    this.erroreGeocodifica = '';
    this.indirizzoSubject.next(indirizzo);
  }

  carica(): void {
    this.adminService.listaPattuglie().subscribe(p => this.pattuglie = p);
  }

  toggleEspansa(id: number): void {
    this.espansa = this.espansa === id ? null : id;
  }

  creaPattuglia(): void {
    this.messaggio = '';
    const { nome, descrizione, veicoloTarga, consumoMedioL100Km, tipoCarburante } = this.nuovaPattuglia;
    this.adminService.creaPattuglia(nome, descrizione, veicoloTarga, consumoMedioL100Km, tipoCarburante).subscribe({
      next: () => {
        this.messaggio = 'Pattuglia creata con successo.';
        this.nuovaPattuglia = { nome: '', descrizione: '', veicoloTarga: '', consumoMedioL100Km: 10, tipoCarburante: 'BENZINA' };
        this.carica();
      },
      error: () => this.messaggio = 'Errore durante la creazione della pattuglia.'
    });
  }

  toggleStato(pattuglia: Pattuglia): void {
    this.adminService.impostaStatoPattuglia(pattuglia.id, !pattuglia.attiva).subscribe(() => this.carica());
  }

  creaObiettivo(pattugliaId: number): void {
    this.messaggio = '';
    this.adminService.creaObiettivo(pattugliaId, this.nuovoObiettivo).subscribe({
      next: () => {
        this.messaggio = 'Obiettivo aggiunto con successo.';
        this.nuovoObiettivo = { nome: '', indirizzo: '', latitudine: 0, longitudine: 0 };
        this.coordinateTrovate = false;
        this.indirizzoNormalizzato = '';
      },
      error: () => this.messaggio = 'Errore durante la creazione dell\'obiettivo.'
    });
  }
}
