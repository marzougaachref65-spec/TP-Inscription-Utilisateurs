import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { User } from '../models/user.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-verif-email',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './verif-email.html',
  styleUrl: './verif-email.css',
})
export class VerifEmail {

  code = '';
  user: User = new User();
  err = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {
    this.user = this.authService.getRegistredUser();
  }

  onValidateEmail(): void {
    if (!/^\d{6}$/.test(this.code.trim())) {
      this.err = 'Veuillez saisir le code à 6 chiffres reçu par email.';
      this.toastr.error(this.err, 'Validation');
      this.cdr.markForCheck();
      return;
    }

    this.err = '';
    this.loading = true;
    this.cdr.markForCheck();

    this.authService.validateEmail(this.code.trim()).subscribe({
      next: () => {
        this.toastr.success('Email validé avec succès', 'Confirmation');

        // Connexion automatique apres validation (le role USER est deja attribue).
        const registered = this.authService.getRegistredUser();
        if (!registered?.username || !registered?.password) {
          this.loading = false;
          this.router.navigate(['/login']);
          return;
        }

        this.authService.login(registered).subscribe({
          next: (data) => {
            const jwToken = data.headers.get('Authorization')!;
            this.authService.saveToken(jwToken);
            this.loading = false;
            this.router.navigate(['/']);
          },
          error: () => {
            this.loading = false;
            this.router.navigate(['/login']);
          }
        });
      },
      error: (err: any) => {
        this.loading = false;
        this.err = this.extractError(err);
        this.toastr.error(this.err, 'Validation');
        this.cdr.markForCheck();
      }
    });
  }

  private extractError(err: any): string {
    const code = err?.error?.errorCode;
    if (code === 'INVALID_TOKEN') {
      return 'Code invalide !';
    }
    if (code === 'EXPIRED_TOKEN') {
      return 'Code expiré !';
    }
    if (err?.error?.message) {
      return err.error.message;
    }
    return 'Impossible de valider le code. Veuillez réessayer.';
  }
}
