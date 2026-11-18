import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { User } from '../models/user.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  user = new User();
  erreur = 0;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  // Atelier 16bis : login via le web service JWT (Authorization header).
  onLoggedin(): void {
    this.authService.login(this.user).subscribe({
      next: (data) => {
        const jwToken = data.headers.get('Authorization')!;
        this.authService.saveToken(jwToken);
        this.router.navigate(['/']);
      },
      error: (err: any) => {
        console.error('Login refusi par le backend', err);
        this.erreur = 1;
        this.cdr.markForCheck();
      }
    });
  }
}
