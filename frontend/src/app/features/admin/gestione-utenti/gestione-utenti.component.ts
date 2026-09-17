import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Utente, NuovoUtente } from '../../../core/services/admin.service';

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
        <tr *ngFor="let u of utenti">
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
            <button class="btn-elimina" (click)="elimina(u)">Elimina</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
  styleUrls: ['./gestione-utenti.component.css']
})
export class GestioneUtentiComponent implements OnInit {
  utenti: Utente[] = [];
  messaggio = '';

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
