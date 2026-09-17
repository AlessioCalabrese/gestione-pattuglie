import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { PattugliaService, Obiettivo, RisultatoOttimizzazione } from '../../core/services/pattuglia.service';

@Component({
  selector: 'app-lista-obiettivi',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <div class="header">
        <h1>Obiettivi da raggiungere</h1>
        <button class="btn-ottimizza" (click)="ottimizzaRotta()" [disabled]="ottimizzando">
          {{ ottimizzando ? 'Calcolo in corso…' : 'Ottimizza rotta' }}
        </button>
      </div>

      <p class="messaggio" *ngIf="messaggio">{{ messaggio }}</p>

      <div class="riepilogo-risparmio" *ngIf="risultatoOttimizzazione as r">
        <h3>Percorso ottimizzato per risparmio carburante</h3>
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
          Stima basata su distanza in linea d'aria e consumo medio del veicolo configurato per la pattuglia.
        </p>
      </div>

      <div class="lista">
        <div class="obiettivo" *ngFor="let o of obiettivi" [class.flaggato]="o.flaggatoOggi">
          <div class="ordine" *ngIf="o.ordineVisita">{{ o.ordineVisita }}</div>
          <div class="dettagli">
            <h3>{{ o.nome }}</h3>
            <p>{{ o.indirizzo }}</p>
            <span class="stato" *ngIf="o.flaggatoOggi">
              ✓ Ultimo passaggio: {{ o.ultimoFlagDataOra }}
            </span>
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

  constructor(private route: ActivatedRoute, private pattugliaService: PattugliaService) {}

  ngOnInit(): void {
    this.pattugliaId = Number(this.route.snapshot.paramMap.get('pattugliaId'));
    this.caricaObiettivi();
  }

  caricaObiettivi(): void {
    this.pattugliaService.obiettiviDiPattuglia(this.pattugliaId).subscribe(o => this.obiettivi = o);
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
