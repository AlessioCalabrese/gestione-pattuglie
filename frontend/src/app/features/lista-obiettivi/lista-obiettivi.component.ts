import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PattugliaService, Obiettivo, RisultatoOttimizzazione, FlagRequest } from '../../core/services/pattuglia.service';
import { PosizioneService } from '../../core/services/posizione.service';
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

          <div class="navigatore" *ngIf="r.tratteNavigatore?.length">
            <h3>Navigatore Google Maps</h3>
            <p class="hint">
              Apri il link dal telefono per avviare la navigazione: le tappe sono nell'ordine consigliato e
              Google Maps non le riordina.
              <ng-container *ngIf="r.tratteNavigatore.length > 1">
                Il percorso è diviso in {{ r.tratteNavigatore.length }} tratte (Google Maps accetta al massimo
                10 tappe per volta): finita una tratta, apri la successiva.
              </ng-container>
            </p>
            <div class="tratta" *ngFor="let t of r.tratteNavigatore">
              <a class="btn-maps" [href]="t.url" target="_blank" rel="noopener">
                {{ r.tratteNavigatore.length > 1 ? 'Tratta ' + t.numero + ' — ' : '' }}Apri in Google Maps
              </a>
              <span class="tappe">{{ t.tappe.join(' → ') }}</span>
            </div>
          </div>
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
              <span class="badge-tipo">{{ o.tipoObiettivo === 'BIGLIETTAZIONE' ? 'Bigliettazione' : 'Ispezione' }}</span>
              <span class="badge-priorita" *ngIf="o.priorita">Priorità</span>
              <span class="badge-fuori-servizio" *ngIf="!o.inServizioOra">Fuori servizio ora</span>
            </h3>
            <p>
              {{ o.indirizzo }}
              <span class="badge-pattuglia" *ngIf="o.pattugliaId !== pattugliaId">· {{ o.pattugliaNome }}</span>
            </p>
            <div class="fasce-oggi" *ngIf="o.fasceOggi.length">
              <span class="fascia-oggi" *ngFor="let f of o.fasceOggi"
                    [class.completa]="f.completata" [class.in-corso]="f.inCorsoOra">
                {{ (f.oraInizio || '00:00').substring(0, 5) }}–{{ (f.oraFine || '24:00').substring(0, 5) }}:
                {{ f.numeroFlag }}/{{ f.ripetizioniRichieste }} completati
                <ng-container *ngIf="f.continuaDaIeri"> · da ieri notte</ng-container>
                <ng-container *ngIf="f.inCorsoOra"> · in corso</ng-container>
              </span>
            </div>
            <span class="stato" *ngIf="o.ultimoFlagDataOra">Ultimo flag oggi: {{ o.ultimoFlagDataOra }}</span>
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
    private posizioneService: PosizioneService,
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

  flagObiettivo(obiettivo: Obiettivo): void {
    this.messaggio = '';
    this.flaggando = obiettivo.id;

    this.posizioneService.rileva().then(posizione => {
      const daGps = posizione.fonte === 'GPS';
      const request: FlagRequest = {
        obiettivoId: obiettivo.id,
        latitudine: posizione.latitudine,
        longitudine: posizione.longitudine,
        precisioneMetri: posizione.precisioneMetri ?? undefined,
        // Le posizioni non GPS restano riconoscibili nello storico dei flag
        note: daGps ? undefined : PosizioneService.descrizioneFonte(posizione)
      };

      this.pattugliaService.flagObiettivo(this.pattugliaId, request).subscribe({
        next: () => {
          this.messaggio = `Obiettivo "${obiettivo.nome}" registrato.`
            + (daGps ? '' : ` ${PosizioneService.descrizioneFonte(posizione)}.`);
          this.flaggando = null;
          this.caricaObiettivi();
        },
        error: () => {
          this.messaggio = 'Errore durante la registrazione del flag.';
          this.flaggando = null;
        }
      });
    }).catch(() => {
      this.messaggio = 'Impossibile rilevare la posizione: GPS non disponibile e posizione dalla rete non ricavabile.';
      this.flaggando = null;
    });
  }

  ottimizzaRotta(): void {
    this.messaggio = '';
    this.risultatoOttimizzazione = null;
    this.ottimizzando = true;

    this.posizioneService.rileva().then(posizione => {
      this.pattugliaService.ottimizzaRotta(this.pattugliaId, posizione.latitudine, posizione.longitudine).subscribe({
        next: (risultato) => {
          this.obiettivi = risultato.obiettivi;
          this.risultatoOttimizzazione = risultato;
          this.ottimizzando = false;
          if (posizione.fonte !== 'GPS') {
            this.messaggio = `${PosizioneService.descrizioneFonte(posizione)}: il punto di partenza della rotta è approssimativo.`;
          }
        },
        error: () => {
          this.messaggio = 'Errore durante il calcolo della rotta.';
          this.ottimizzando = false;
        }
      });
    }).catch(() => {
      this.messaggio = 'Impossibile rilevare la posizione di partenza: GPS non disponibile e posizione dalla rete non ricavabile.';
      this.ottimizzando = false;
    });
  }
}
