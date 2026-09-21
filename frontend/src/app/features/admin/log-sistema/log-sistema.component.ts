import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, LogSistema } from '../../../core/services/admin.service';

@Component({
  selector: 'app-log-sistema',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Log di sistema</h1>

    <form class="filtri" (ngSubmit)="cerca()">
      <label>
        Dal
        <input type="date" [(ngModel)]="dal" name="dal" [max]="al || ''" />
      </label>
      <label>
        Al
        <input type="date" [(ngModel)]="al" name="al" [min]="dal || ''" />
      </label>
      <label>
        Tipo evento
        <select [(ngModel)]="tipo" name="tipo">
          <option value="">Tutti</option>
          <option *ngFor="let t of tipiEvento" [value]="t">{{ t }}</option>
        </select>
      </label>
      <button type="submit">Cerca</button>
      <button type="button" class="secondario" (click)="azzera()">Azzera filtri</button>
    </form>

    <p class="messaggio" *ngIf="errore">{{ errore }}</p>

    <p class="conteggio" *ngIf="caricato">{{ totale }} eventi trovati</p>

    <table class="tabella-log" *ngIf="eventi.length > 0">
      <thead>
        <tr>
          <th>Data e ora</th>
          <th>Utente</th>
          <th>Evento</th>
          <th>Descrizione</th>
          <th>IP</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let e of eventi">
          <td class="nowrap">{{ e.dataOra | date: 'dd/MM/yyyy HH:mm:ss' }}</td>
          <td>
            <ng-container *ngIf="e.username; else sistema">
              {{ e.nomeUtente }} <small>({{ e.username }})</small>
            </ng-container>
            <ng-template #sistema><em>sistema</em></ng-template>
          </td>
          <td><span class="tipo">{{ e.tipoEvento }}</span></td>
          <td>{{ e.descrizione }}</td>
          <td class="nowrap">{{ e.indirizzoIp }}</td>
        </tr>
      </tbody>
    </table>

    <p class="hint" *ngIf="caricato && eventi.length === 0">Nessun evento nel periodo selezionato.</p>

    <div class="paginazione" *ngIf="totalePagine > 1">
      <button type="button" (click)="vaiAPagina(pagina - 1)" [disabled]="pagina === 0">« Precedente</button>
      <span>Pagina {{ pagina + 1 }} di {{ totalePagine }}</span>
      <button type="button" (click)="vaiAPagina(pagina + 1)" [disabled]="pagina >= totalePagine - 1">Successiva »</button>
    </div>
  `,
  styleUrls: ['./log-sistema.component.css']
})
export class LogSistemaComponent implements OnInit {
  dal = '';
  al = '';
  tipo = '';
  tipiEvento: string[] = [];

  eventi: LogSistema[] = [];
  pagina = 0;
  totalePagine = 0;
  totale = 0;
  caricato = false;
  errore = '';

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.tipiEventoLog().subscribe(tipi => this.tipiEvento = tipi);
    this.carica();
  }

  cerca(): void {
    this.pagina = 0;
    this.carica();
  }

  azzera(): void {
    this.dal = '';
    this.al = '';
    this.tipo = '';
    this.cerca();
  }

  vaiAPagina(pagina: number): void {
    this.pagina = Math.max(0, Math.min(pagina, this.totalePagine - 1));
    this.carica();
  }

  private carica(): void {
    this.errore = '';
    this.adminService.listaLog({ dal: this.dal, al: this.al, tipo: this.tipo }, this.pagina).subscribe({
      next: risultato => {
        this.eventi = risultato.contenuto;
        this.totalePagine = risultato.totalePagine;
        this.totale = risultato.totaleElementi;
        this.caricato = true;
      },
      error: err => {
        this.eventi = [];
        this.totalePagine = 0;
        this.totale = 0;
        this.errore = err?.error?.errore ?? 'Errore durante il caricamento del log di sistema.';
      }
    });
  }
}
