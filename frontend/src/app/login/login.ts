import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { User } from '../models/user.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  user = new User();
  erreur = 0;
  message = 'Login ou mot de passe erronés..';

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  // Atelier 16bis : login via le web service JWT (Authorization header).
  onLoggedin(): void {
    this.message = 'Login ou mot de passe erronés..';
    this.authService.login(this.user).subscribe({
      next: (data) => {
        const jwToken = data.headers.get('Authorization')!;
        this.authService.saveToken(jwToken);
        this.toastr.success('Bienvenue ' + this.user.username, 'Connexion');
        this.router.navigate(['/']);
      },
      error: (err: any) => {
        console.error('Login refusi par le backend', err);
        this.erreur = 1;
        const cause = err?.error?.errorCause;
        if (cause === 'disabled') {
          this.message = 'Utilisateur désactivé, veuillez valider votre email ou contacter votre administrateur.';
        } else if (err?.error?.message) {
          this.message = err.error.message;
        }
        this.toastr.error(this.message, 'Connexion');
        this.cdr.markForCheck();
      }
    });
  }
}
