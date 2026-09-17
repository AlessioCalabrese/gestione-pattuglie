import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { SelezionePattugliaComponent } from './features/selezione-pattuglia/selezione-pattuglia.component';
import { ListaObiettiviComponent } from './features/lista-obiettivi/lista-obiettivi.component';
import { AdminDashboardComponent } from './features/admin/dashboard/admin-dashboard.component';
import { GestioneUtentiComponent } from './features/admin/gestione-utenti/gestione-utenti.component';
import { GestionePattuglieComponent } from './features/admin/gestione-pattuglie/gestione-pattuglie.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'selezione-pattuglia', component: SelezionePattugliaComponent, canActivate: [authGuard] },
  { path: 'obiettivi/:pattugliaId', component: ListaObiettiviComponent, canActivate: [authGuard] },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [adminGuard],
    children: [
      { path: 'utenti', component: GestioneUtentiComponent },
      { path: 'pattuglie', component: GestionePattuglieComponent },
      { path: '', redirectTo: 'utenti', pathMatch: 'full' }
    ]
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
