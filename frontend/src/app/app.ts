import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from './services/auth.service';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit {

  protected readonly title = 'Gestion des Joueurs et des Équipes';

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Atelier 16bis : on charge le JWT (et on le decode) au demarrage.
    this.authService.loadToken();
    if (!this.authService.getToken() || this.authService.isTokenExpired()) {
      this.router.navigate(['/login']);
    }
  }

  onLogout(): void {
    this.authService.logout();
  }
}
