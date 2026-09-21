import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Utente, NuovoUtente, Pattuglia, PattugliaRicerca } from '../../../core/services/admin.service';

@Component({
  selector: 'app-gestione-utenti',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Gestione Utenti</h1>

    <form class="form-nuovo" (ngSubmit)="creaUtente()">
      <input type="text" [(ngModel)]="nuovo.username" name="username" placeholder="Username" required />
      <input type="password" [(ngModel)]="nuovo.password" name="password" placeholder="Password" required />
      <input type="text" [(ngModel)]="nuovo.nome" name="nome" placeholder="Nome" required />
      <input type="text" [(ngModel)]="nuovo.cognome" name="cognome" placeholder="Cognome" required />
      <input type="text" [(ngModel)]="nuovo.nfcTagId" name="nfcTagId" placeholder="Tag NFC (opzionale)" />
      <select [(ngModel)]="nuovo.ruolo" name="ruolo">
        <option value="PATTUGLIA">Pattuglia</option>
        <option value="ADMIN">Admin</option>
      </select>
      <button type="submit">Crea utente</button>
    </form>

    <p class="messaggio" *ngIf="messaggio">{{ messaggio }}</p>

    <table>
      <thead>
        <tr>
          <th>Username</th>
          <th>Nome</th>
          <th>Ruolo</th>
          <th>Stato</th>
          <th>Azioni</th>
        </tr>
      </thead>
      <tbody>
        <ng-container *ngFor="let u of utenti">
          <tr>
            <td>{{ u.username }}</td>
            <td>{{ u.nome }} {{ u.cognome }}</td>
            <td>{{ u.ruolo }}</td>
            <td>
              <span [class.badge-attivo]="u.abilitato" [class.badge-bloccato]="!u.abilitato">
                {{ u.abilitato ? 'Abilitato' : 'Bloccato' }}
              </span>
            </td>
            <td>
              <button (click)="toggleAbilitazione(u)">
                {{ u.abilitato ? 'Blocca' : 'Sblocca' }}
              </button>
              <button *ngIf="u.ruolo === 'PATTUGLIA'" (click)="togglePattuglie(u)">
                {{ espanso === u.id ? 'Chiudi pattuglie' : 'Pattuglie' }}
              </button>
              <button class="btn-elimina" (click)="elimina(u)">Elimina</button>
            </td>
          </tr>

          <tr class="riga-pattuglie" *ngIf="espanso === u.id">
            <td colspan="5">
              <p class="hint">
                Pattuglie preferite di {{ u.nome }} {{ u.cognome }}. Se ne ha, nella selezione vede solo queste
                (può comunque mostrarle tutte e modificarle da sé); se non ne ha, può scegliere tra tutte.
              </p>

              <strong>Associate ({{ associate.length }})</strong>
              <p class="hint" *ngIf="associate.length === 0">Nessuna: l'utente può scegliere tra tutte le pattuglie.</p>
              <ul class="lista-associate" *ngIf="associate.length > 0">
                <li *ngFor="let p of associate">
                  <span>{{ p.nome }} <small>{{ p.descrizione }}</small>
                    <em *ngIf="!p.attiva"> — disattivata</em></span>
                  <button type="button" class="btn-elimina" (click)="rimuovi(u, p)">Rimuovi</button>
                </li>
              </ul>

              <strong>Aggiungi pattuglia</strong>
              <div class="ricerca-pattuglie">
                <input type="search" [(ngModel)]="ricercaPattuglia" [name]="'ricerca' + u.id"
                       placeholder="Cerca per nome, descrizione o obiettivo" (keyup.enter)="cercaPattuglie()" />
                <button type="button" (click)="cercaPattuglie()">Cerca</button>
              </div>
              <ul class="lista-associate" *ngIf="risultati.length > 0">
                <li *ngFor="let p of risultati">
                  <span>{{ p.nome }} <small>{{ p.descrizione }}</small>
                    <em *ngIf="!p.attiva"> — disattivata</em></span>
                  <button type="button" [disabled]="giaAssociata(p.id)" (click)="associa(u, p)">
                    {{ giaAssociata(p.id) ? 'Già associata' : 'Aggiungi' }}
                  </button>
                </li>
              </ul>
              <p class="hint" *ngIf="cercato && risultati.length === 0">Nessuna pattuglia trovata.</p>
              <div class="paginazione" *ngIf="totalePagineRisultati > 1">
                <button type="button" (click)="vaiAPagina(paginaRisultati - 1)" [disabled]="paginaRisultati === 0">« Precedente</button>
                <span>Pagina {{ paginaRisultati + 1 }} di {{ totalePagineRisultati }}</span>
                <button type="button" (click)="vaiAPagina(paginaRisultati + 1)" [disabled]="paginaRisultati >= totalePagineRisultati - 1">Successiva »</button>
              </div>
            </td>
          </tr>
        </ng-container>
      </tbody>
    </table>
  `,
  styleUrls: ['./gestione-utenti.component.css']
})
export class GestioneUtentiComponent implements OnInit {
  utenti: Utente[] = [];
  messaggio = '';

  // Pannello "Pattuglie" dell'utente espanso
  espanso: number | null = null;
  associate: Pattuglia[] = [];
  ricercaPattuglia = '';
  risultati: PattugliaRicerca[] = [];
  paginaRisultati = 0;
  totalePagineRisultati = 0;
  cercato = false;

  nuovo: NuovoUtente = {
    username: '', password: '', nome: '', cognome: '', ruolo: 'PATTUGLIA', nfcTagId: ''
  };

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.carica();
  }

  carica(): void {
    this.adminService.listaUtenti().subscribe(u => this.utenti = u);
  }

  creaUtente(): void {
    this.messaggio = '';
    this.adminService.creaUtente(this.nuovo).subscribe({
      next: () => {
        this.messaggio = 'Utente creato con successo.';
        this.nuovo = { username: '', password: '', nome: '', cognome: '', ruolo: 'PATTUGLIA', nfcTagId: '' };
        this.carica();
      },
      error: (err) => this.messaggio = err.error?.errore ?? 'Errore durante la creazione.'
    });
  }

  // ---- Pattuglie preferite dell'utente ----

  togglePattuglie(utente: Utente): void {
    this.espanso = this.espanso === utente.id ? null : utente.id;
    this.associate = [];
    this.risultati = [];
    this.ricercaPattuglia = '';
    this.cercato = false;
    this.paginaRisultati = 0;
    this.totalePagineRisultati = 0;
    if (this.espanso !== null) {
      this.caricaAssociate(utente);
      this.cercaPattuglie(); // mostra subito le prime pattuglie tra cui scegliere
    }
  }

  private caricaAssociate(utente: Utente): void {
    this.adminService.pattuglieDiUtente(utente.id).subscribe({
      next: p => this.associate = p,
      error: err => this.messaggio = err.error?.errore ?? 'Errore nel caricamento delle pattuglie dell\'utente.'
    });
  }

  giaAssociata(pattugliaId: number): boolean {
    return this.associate.some(p => p.id === pattugliaId);
  }

  cercaPattuglie(): void {
    this.paginaRisultati = 0;
    this.caricaRisultati();
  }

  vaiAPagina(pagina: number): void {
    this.paginaRisultati = Math.max(0, Math.min(pagina, this.totalePagineRisultati - 1));
    this.caricaRisultati();
  }

  private caricaRisultati(): void {
    this.adminService.cercaPattuglie(this.ricercaPattuglia.trim(), this.paginaRisultati).subscribe({
      next: r => {
        this.risultati = r.contenuto;
        this.totalePagineRisultati = r.totalePagine;
        this.cercato = true;
      },
      error: err => this.messaggio = err.error?.errore ?? 'Errore nella ricerca delle pattuglie.'
    });
  }

  associa(utente: Utente, pattuglia: Pattuglia): void {
    this.messaggio = '';
    this.adminService.associaPattuglia(utente.id, pattuglia.id).subscribe({
      next: () => this.caricaAssociate(utente),
      error: err => this.messaggio = err.error?.errore ?? 'Impossibile associare la pattuglia.'
    });
  }

  rimuovi(utente: Utente, pattuglia: Pattuglia): void {
    this.messaggio = '';
    this.adminService.rimuoviPattuglia(utente.id, pattuglia.id).subscribe({
      next: () => this.caricaAssociate(utente),
      error: err => this.messaggio = err.error?.errore ?? 'Impossibile rimuovere la pattuglia.'
    });
  }

  toggleAbilitazione(utente: Utente): void {
    this.adminService.impostaAbilitazione(utente.id, !utente.abilitato).subscribe(() => this.carica());
  }

  elimina(utente: Utente): void {
    if (!confirm(`Eliminare definitivamente l'utente "${utente.username}"?`)) return;

    this.adminService.eliminaUtente(utente.id).subscribe({
      next: () => this.carica(),
      error: () => this.messaggio = 'Impossibile eliminare: utente con storico associato. Bloccalo invece.'
    });
  }
}
