import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface LoginResponse {
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(
      tap(res => localStorage.setItem(this.TOKEN_KEY, res.token))
    );
  }

  /** Login con il codice del tag NFC; manuale=true se il codice è stato digitato invece che letto dal lettore. */
  loginNfc(nfcTagId: string, manuale = false): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login-nfc', { nfcTagId, manuale }).pipe(
      tap(res => localStorage.setItem(this.TOKEN_KEY, res.token))
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/auth/logout', {}).pipe(
      tap(() => localStorage.removeItem(this.TOKEN_KEY))
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /** Decodifica il payload del JWT (senza validare la firma: solo per leggere ruolo/nome lato UI). */
  getRuolo(): string | null {
    return this.decodificaPayload()?.ruolo ?? null;
  }

  /** "Nome Cognome" dell'utente loggato, per mostrarlo in pagina; null se non decodificabile. */
  getNomeCompleto(): string | null {
    const payload = this.decodificaPayload();
    if (!payload?.nome && !payload?.cognome) {
      return null;
    }
    return [payload.nome, payload.cognome].filter(v => !!v).join(' ');
  }

  private decodificaPayload(): { ruolo?: string; nome?: string; cognome?: string } | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }
}
