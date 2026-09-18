import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PattugliaService, Pattuglia } from '../../core/services/pattuglia.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-selezione-pattuglia',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <div class="header">
        <h1>Seleziona la tua pattuglia</h1>
        <button class="btn-esci" (click)="esci()">Esci</button>
      </div>
      <div class="lista-pattuglie">
        <div class="card-pattuglia" *ngFor="let p of pattuglie" (click)="seleziona(p)">
          <h2>{{ p.nome }}</h2>
          <p>{{ p.descrizione }}</p>
        </div>
      </div>
      <p *ngIf="pattuglie.length === 0">Nessuna pattuglia disponibile per il tuo utente.</p>
    </div>
  `,
  styleUrls: ['./selezione-pattuglia.component.css']
})
export class SelezionePattugliaComponent implements OnInit {
  pattuglie: Pattuglia[] = [];

  constructor(
    private pattugliaService: PattugliaService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.pattugliaService.pattuglieSelezionabili().subscribe(p => this.pattuglie = p);
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
