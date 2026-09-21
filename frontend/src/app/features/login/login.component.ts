import { Component, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

type ModalitaAccesso = 'password' | 'nfc';

// Dichiarazione minimale per l'API Web NFC (non ancora in tutti i lib.dom.d.ts)
declare global {
  interface Window {
    NDEFReader?: any;
  }
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="login-container">
      <div class="logo-placeholder">IV</div>
      <h1>Accesso Pattuglie</h1>

      <div class="selettore-modalita">
        <button
          [class.attivo]="modalita === 'password'"
          (click)="impostaModalita('password')">
          Username e Password
        </button>
        <button
          [class.attivo]="modalita === 'nfc'"
          (click)="impostaModalita('nfc')">
          Tag NFC
        </button>
      </div>

      <!-- Login con username/password -->
      <form *ngIf="modalita === 'password'" (ngSubmit)="onSubmitPassword()">
        <input type="text" name="username" [(ngModel)]="username" placeholder="Username" required />
        <input type="password" name="password" [(ngModel)]="password" placeholder="Password" required />
        <button type="submit" [disabled]="caricamento">Accedi</button>
      </form>

      <!-- Login con tag NFC -->
      <div *ngIf="modalita === 'nfc'" class="area-nfc">
        <p *ngIf="!nfcSupportato" class="avviso">
          Il browser/dispositivo non supporta la lettura NFC (Web NFC API): puoi inserire a mano il codice del tag.
          Su Android la lettura funziona con Chrome.
        </p>
        <p *ngIf="nfcSupportato && !inAscolto">
          Avvicina il tag NFC al dispositivo per accedere.
        </p>
        <button *ngIf="nfcSupportato" (click)="avviaLetturaNfc()" [disabled]="inAscolto">
          {{ inAscolto ? 'In ascolto…' : 'Avvia lettura NFC' }}
        </button>

        <!-- Inserimento manuale del codice del tag (utile se la lettura NFC non è disponibile) -->
        <p class="separatore">{{ nfcSupportato ? 'oppure inserisci il codice del tag' : 'Inserisci il codice del tag' }}</p>
        <form class="form-codice-nfc" (ngSubmit)="onSubmitCodiceNfc()">
          <input type="text" name="codiceNfc" [(ngModel)]="codiceNfc" autocomplete="off" autocapitalize="off"
                 placeholder="Codice tag NFC (es. 04:a1:b2:c3:d4:e5:f6)" required />
          <button type="submit" [disabled]="caricamento || !codiceNfc.trim()">Accedi con il codice</button>
        </form>
      </div>

      <p class="errore" *ngIf="messaggioErrore">{{ messaggioErrore }}</p>
    </div>
  `,
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnDestroy {
  modalita: ModalitaAccesso = 'password';

  username = '';
  password = '';
  caricamento = false;
  messaggioErrore = '';

  codiceNfc = '';

  nfcSupportato = typeof window !== 'undefined' && !!window.NDEFReader;
  inAscolto = false;
  private nfcController: AbortController | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  impostaModalita(modalita: ModalitaAccesso): void {
    this.modalita = modalita;
    this.messaggioErrore = '';
    this.fermaLetturaNfc();
  }

  onSubmitPassword(): void {
    this.caricamento = true;
    this.messaggioErrore = '';

    this.authService.login(this.username, this.password).subscribe({
      next: () => this.dopoLoginRiuscito(),
      error: () => {
        this.messaggioErrore = 'Credenziali non valide o utente disabilitato';
        this.caricamento = false;
      }
    });
  }

  /** Login con il codice del tag digitato a mano: vale per qualunque ruolo, come la lettura NFC. */
  onSubmitCodiceNfc(): void {
    const codice = this.codiceNfc.trim();
    if (!codice) {
      return;
    }
    this.caricamento = true;
    this.messaggioErrore = '';

    this.authService.loginNfc(codice, true).subscribe({
      next: () => this.dopoLoginRiuscito(),
      error: () => {
        this.messaggioErrore = 'Tag NFC non riconosciuto o utente disabilitato';
        this.caricamento = false;
      }
    });
  }

  async avviaLetturaNfc(): Promise<void> {
    if (!this.nfcSupportato) return;

    this.messaggioErrore = '';
    this.inAscolto = true;
    this.nfcController = new AbortController();

    try {
      const reader = new window.NDEFReader!();
      await reader.scan({ signal: this.nfcController.signal });

      reader.onreading = (event: any) => {
        // Usiamo il serialNumber del tag come identificativo univoco (nfc_tag_id lato BE).
        const tagId: string = event.serialNumber;
        this.inAscolto = false;
        this.authService.loginNfc(tagId).subscribe({
          next: () => this.dopoLoginRiuscito(),
          error: () => this.messaggioErrore = 'Tag NFC non riconosciuto o utente disabilitato'
        });
      };

      reader.onreadingerror = () => {
        this.messaggioErrore = 'Errore durante la lettura del tag NFC. Riprova.';
        this.inAscolto = false;
      };
    } catch (err) {
      this.messaggioErrore = 'Impossibile avviare la lettura NFC (permesso negato o non disponibile).';
      this.inAscolto = false;
    }
  }

  private fermaLetturaNfc(): void {
    this.nfcController?.abort();
    this.nfcController = null;
    this.inAscolto = false;
  }

  private dopoLoginRiuscito(): void {
    const ruolo = this.authService.getRuolo();
    this.router.navigate([ruolo === 'ADMIN' ? '/admin' : '/selezione-pattuglia']);
  }

  ngOnDestroy(): void {
    this.fermaLetturaNfc();
  }
}
