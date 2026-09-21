import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PattugliaService, Pattuglia } from '../../core/services/pattuglia.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-selezione-pattuglia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h1>Seleziona la tua pattuglia</h1>
        <button class="btn-esci" (click)="esci()">Esci</button>
      </div>

      <div class="barra-strumenti">
        <input type="search" [(ngModel)]="ricerca" name="ricerca" placeholder="Cerca pattuglia per nome o descrizione" />
        <button type="button" class="btn-vista" *ngIf="haPreferite" (click)="cambiaVista()">
          {{ mostraTutte ? 'Mostra solo le preferite' : 'Mostra tutte le pattuglie' }}
        </button>
      </div>

      <p class="suggerimento" *ngIf="!haPreferite && pattuglie.length > 0">
        Tocca la stella ☆ per scegliere le tue pattuglie preferite: da quel momento vedrai solo quelle
        (potrai sempre mostrarle tutte).
      </p>

      <div class="lista-pattuglie">
        <div class="card-pattuglia" *ngFor="let p of pattuglieFiltrate" (click)="seleziona(p)">
          <button type="button" class="stella" [class.attiva]="p.preferita"
                  [attr.aria-label]="p.preferita ? 'Rimuovi dalle preferite' : 'Aggiungi alle preferite'"
                  (click)="togglePreferita(p, $event)">
            {{ p.preferita ? '★' : '☆' }}
          </button>
          <h2>{{ p.nome }}</h2>
          <p>{{ p.descrizione }}</p>
        </div>
      </div>

      <p *ngIf="caricato && pattuglie.length === 0">Nessuna pattuglia disponibile.</p>
      <p *ngIf="pattuglie.length > 0 && pattuglieFiltrate.length === 0">Nessuna pattuglia corrisponde alla ricerca.</p>
      <p class="errore" *ngIf="errore">{{ errore }}</p>
    </div>
  `,
  styleUrls: ['./selezione-pattuglia.component.css']
})
export class SelezionePattugliaComponent implements OnInit {
  pattuglie: Pattuglia[] = [];
  ricerca = '';
  mostraTutte = false;
  caricato = false;
  errore = '';

  constructor(
    private pattugliaService: PattugliaService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carica();
  }

  /** Vero se l'utente ha almeno una pattuglia preferita. */
  get haPreferite(): boolean {
    return this.pattuglie.some(p => p.preferita);
  }

  get pattuglieFiltrate(): Pattuglia[] {
    const testo = this.ricerca.trim().toLowerCase();
    if (!testo) {
      return this.pattuglie;
    }
    return this.pattuglie.filter(p =>
      p.nome.toLowerCase().includes(testo) || (p.descrizione ?? '').toLowerCase().includes(testo));
  }

  private carica(): void {
    this.pattugliaService.pattuglieSelezionabili(this.mostraTutte).subscribe({
      next: p => {
        this.pattuglie = p;
        this.caricato = true;
      },
      error: () => this.errore = 'Errore durante il caricamento delle pattuglie.'
    });
  }

  cambiaVista(): void {
    this.mostraTutte = !this.mostraTutte;
    this.carica();
  }

  togglePreferita(pattuglia: Pattuglia, evento: Event): void {
    evento.stopPropagation(); // non deve selezionare la pattuglia
    this.errore = '';
    const nuovoValore = !pattuglia.preferita;
    this.pattugliaService.impostaPreferita(pattuglia.id, nuovoValore).subscribe({
      next: () => {
        // Scegliendo la prima preferita si resta nell'elenco completo, così se ne possono aggiungere altre
        // di seguito; "Mostra solo le preferite" riporta all'elenco ridotto.
        if (nuovoValore && !this.haPreferite) {
          this.mostraTutte = true;
        }
        this.carica();
      },
      error: () => this.errore = 'Impossibile aggiornare le preferite.'
    });
  }

  seleziona(pattuglia: Pattuglia): void {
    this.router.navigate(['/obiettivi', pattuglia.id]);
  }

  esci(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }
}
