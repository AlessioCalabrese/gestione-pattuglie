import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="admin-layout">
      <nav class="admin-nav">
        <h2>Amministrazione</h2>
        <a routerLink="utenti" routerLinkActive="active">Utenti</a>
        <a routerLink="pattuglie" routerLinkActive="active">Pattuglie e Obiettivi</a>
      </nav>
      <main class="admin-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .admin-layout {
      display: flex;
      min-height: 100vh;
    }
    .admin-nav {
      width: 220px;
      background: #0a1f44;
      color: #fff;
      padding: 1.5rem 1rem;
      flex-shrink: 0;
    }
    .admin-nav h2 {
      font-size: 1.1rem;
      margin-bottom: 1.5rem;
    }
    .admin-nav a {
      display: block;
      color: #cfd8ea;
      text-decoration: none;
      padding: 0.6rem 0.5rem;
      border-radius: 4px;
      margin-bottom: 0.3rem;
    }
    .admin-nav a.active, .admin-nav a:hover {
      background: #c9a227;
      color: #0a1f44;
      font-weight: bold;
    }
    .admin-content {
      flex: 1;
      padding: 2rem;
      background: #f5f6fa;
    }
  `]
})
export class AdminDashboardComponent {}
