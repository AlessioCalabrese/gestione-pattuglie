import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PattugliaService, Obiettivo, RisultatoOttimizzazione } from '../../core/services/pattuglia.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-lista-obiettivi',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <div class="header">
        <h1>Obiettivi da raggiungere</h1>
        <div class="azioni-header">
          <button class="btn-ottimizza" (click)="ottimizzaRotta()" [disabled]="ottimizzando">
            {{ ottimizzando ? 'Calcolo in corso…' : 'Ottimizza rotta' }}
          </button>
          <button class="btn-esci" (click)="esci()">Esci</button>
        </div>
      </div>

      <p class="messaggio" *ngIf="messaggio">{{ messaggio }}</p>

      <p class="messaggio avviso" *ngIf="risultatoOttimizzazione && risultatoOttimizzazione.obiettiviInServizioOggi === 0">
        Nessun obiettivo di questa pattuglia è pianificato per oggi in questo momento: non c'è un percorso da ottimizzare.
      </p>

      <div class="riepilogo-risparmio" *ngIf="risultatoOttimizzazione as r">
        <ng-container *ngIf="r.obiettiviInServizioOggi > 0">
          <h3>Percorso ottimizzato per risparmio carburante (obiettivi di oggi)</h3>
          <div class="griglia-risparmio">
            <div class="voce">
              <span class="valore">{{ r.distanzaOttimizzataKm }} km</span>
              <span class="etichetta">Distanza totale stimata</span>
            </div>
            <div class="voce risparmio">
              <span class="valore">-{{ r.risparmioKm }} km</span>
              <span class="etichetta">Risparmio rispetto all'ordine precedente ({{ r.risparmioPercentuale }}%)</span>
            </div>
            <div class="voce risparmio">
              <span class="valore">-{{ r.risparmioLitri | number: '1.2-2' }} L</span>
              <span class="etichetta">Carburante risparmiato stimato</span>
            </div>
            <div class="voce risparmio">
              <span class="valore">-{{ r.risparmioCosto | number: '1.2-2' }} €</span>
              <span class="etichetta">Costo risparmiato stimato (a {{ r.prezzoCarburanteAlLitro | number: '1.2-2' }} €/L)</span>
            </div>
          </div>
          <p class="hint">
            Stima basata su distanza in linea d'aria e consumo medio generico. Include solo gli obiettivi
            pianificati per oggi in questa fascia oraria — gli altri restano visibili sotto come "fuori servizio".
          </p>
        </ng-container>
      </div>

      <div class="lista">
        <div class="obiettivo" *ngFor="let o of obiettivi"
             [class.flaggato]="o.completatoOggi"
             [class.prioritario]="o.priorita"
             [class.fuori-servizio]="!o.inServizioOra">
          <div class="ordine" *ngIf="o.ordineVisita">{{ o.ordineVisita }}</div>
          <div class="dettagli">
            <h3>
              {{ o.nome }}
              <span class="badge-priorita" *ngIf="o.priorita">Priorità</span>
              <span class="badge-fuori-servizio" *ngIf="!o.inServizioOra">Fuori servizio ora</span>
            </h3>
            <p>{{ o.indirizzo }}</p>
            <p class="pianificazione" *ngIf="o.oraInizio || o.oraFine || o.giorniAttivi.length">
              <span *ngIf="o.giorniAttivi.length">{{ formattaGiorni(o.giorniAttivi) }}</span>
              <span *ngIf="o.oraInizio || o.oraFine">
                {{ o.giorniAttivi.length ? '·' : '' }} {{ o.oraInizio || '00:00' }}–{{ o.oraFine || '24:00' }}
              </span>
            </p>
            <span class="stato progresso" [class.completo]="o.completatoOggi">
              {{ o.numeroFlagOggi }}/{{ o.ripetizioniGiornaliere }} completati oggi
              <ng-container *ngIf="o.ultimoFlagDataOra"> — ultimo: {{ o.ultimoFlagDataOra }}</ng-container>
            </span>
            <a class="btn-whatsapp" *ngIf="o.whatsappUrl" [href]="o.whatsappUrl" target="_blank" rel="noopener">
              Avvisa su WhatsApp
            </a>
          </div>
          <button class="btn-flag" (click)="flagObiettivo(o)" [disabled]="flaggando === o.id">
            {{ flaggando === o.id ? '...' : 'Flag' }}
          </button>
        </div>
      </div>

      <p *ngIf="obiettivi.length === 0">Nessun obiettivo attivo per questa pattuglia.</p>
    </div>
  `,
  styleUrls: ['./lista-obiettivi.component.css']
})
export class ListaObiettiviComponent implements OnInit {
  pattugliaId!: number;
  obiettivi: Obiettivo[] = [];
  flaggando: number | null = null;
  ottimizzando = false;
  messaggio = '';
  risultatoOttimizzazione: RisultatoOttimizzazione | null = null;

  constructor(
    private route: ActivatedRoute,
    private pattugliaService: PattugliaService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.pattugliaId = Number(this.route.snapshot.paramMap.get('pattugliaId'));
    this.caricaObiettivi();
  }

  esci(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login']) // anche se la revoca lato server fallisce, si esce comunque
    });
  }

  caricaObiettivi(): void {
    this.pattugliaService.obiettiviDiPattuglia(this.pattugliaId).subscribe(o => this.obiettivi = o);
  }

  private readonly abbreviazioniGiorni: Record<string, string> = {
    LUNEDI: 'Lun', MARTEDI: 'Mar', MERCOLEDI: 'Mer', GIOVEDI: 'Gio',
    VENERDI: 'Ven', SABATO: 'Sab', DOMENICA: 'Dom'
  };

  formattaGiorni(giorni: string[]): string {
    return giorni.map(g => this.abbreviazioniGiorni[g] ?? g).join(', ');
  }

  flagObiettivo(obiettivo: Obiettivo): void {
    this.messaggio = '';

    if (!navigator.geolocation) {
      this.messaggio = 'Geolocalizzazione non supportata da questo dispositivo.';
      return;
    }

    this.flaggando = obiettivo.id;

    navigator.geolocation.getCurrentPosition(
      (posizione) => {
        const request = {
          obiettivoId: obiettivo.id,
          latitudine: posizione.coords.latitude,
          longitudine: posizione.coords.longitude,
          precisioneMetri: posizione.coords.accuracy
        };

        this.pattugliaService.flagObiettivo(this.pattugliaId, request).subscribe({
          next: () => {
            this.messaggio = `Obiettivo "${obiettivo.nome}" registrato.`;
            this.flaggando = null;
            this.caricaObiettivi();
          },
          error: () => {
            this.messaggio = 'Errore durante la registrazione del flag.';
            this.flaggando = null;
          }
        });
      },
      () => {
        this.messaggio = 'Impossibile rilevare la posizione. Verifica i permessi GPS.';
        this.flaggando = null;
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  }

  ottimizzaRotta(): void {
    this.messaggio = '';
    this.risultatoOttimizzazione = null;

    if (!navigator.geolocation) {
      this.messaggio = 'Geolocalizzazione non supportata da questo dispositivo.';
      return;
    }

    this.ottimizzando = true;

    navigator.geolocation.getCurrentPosition(
      (posizione) => {
        this.pattugliaService.ottimizzaRotta(
          this.pattugliaId,
          posizione.coords.latitude,
          posizione.coords.longitude
        ).subscribe({
          next: (risultato) => {
            this.obiettivi = risultato.obiettivi;
            this.risultatoOttimizzazione = risultato;
            this.ottimizzando = false;
          },
          error: () => {
            this.messaggio = 'Errore durante il calcolo della rotta.';
            this.ottimizzando = false;
          }
        });
      },
      () => {
        this.messaggio = 'Impossibile rilevare la posizione di partenza.';
        this.ottimizzando = false;
      }
    );
  }
}
