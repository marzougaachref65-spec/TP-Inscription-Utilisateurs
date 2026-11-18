import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { User } from '../models/user.model';

// Ateliers 13-16bis + TP 2025 : authentification et inscription cote client.
@Injectable({ providedIn: 'root' })
export class AuthService {

  private helper = new JwtHelperService();
  public token!: string;
  public loggedUser!: string;
  public isloggedIn: Boolean = false;
  public roles!: string[];

  // TP 2025 : utilisateur venant d'etre inscrit, utilise par verifEmail.
  public regitredUser: User = new User();

  constructor(private router: Router, private http: HttpClient) {
    this.loadToken();
  }

  // Atelier 16bis : recuperer le token depuis POST /login
  login(user: User): Observable<HttpResponse<any>> {
    return this.http.post<any>(`${environment.authURL}/login`, user, { observe: 'response' });
  }

  // TP 2025 : creer le compte (desactive) et envoyer le code par email.
  registerUser(user: User): Observable<HttpResponse<any>> {
    return this.http.post<any>(`${environment.authURL}/register`, user, { observe: 'response' });
  }

  // TP 2025 : valider le code a 6 chiffres recu par email.
  validateEmail(code: string): Observable<User> {
    return this.http.get<User>(`${environment.authURL}/verifyEmail/${code}`);
  }

  setRegistredUser(user: User): void {
    this.regitredUser = user;
  }

  getRegistredUser(): User {
    return this.regitredUser;
  }

  saveToken(jwt: string): void {
    if (!jwt) {
      return;
    }
    // Le backend renvoie le token brut ; accepter aussi un token prefixe "Bearer ".
    this.token = jwt.startsWith('Bearer ') ? jwt.substring(7) : jwt;
    localStorage.setItem('jwt', this.token);
    localStorage.setItem('isloggedIn', 'true');
    this.isloggedIn = true;
    this.decodeJWT();
  }

  loadToken(): void {
    const stored = localStorage.getItem('jwt');
    this.token = stored ?? undefined!;
    if (this.token) {
      this.decodeJWT();
    } else {
      this.clearSessionState();
    }
  }

  getToken(): string {
    return this.token;
  }

  decodeJWT(): void {
    if (!this.token) {
      return;
    }
    try {
      const decoded = this.helper.decodeToken(this.token);
      this.roles = decoded?.roles ?? [];
      this.loggedUser = decoded?.sub ?? '';
      localStorage.setItem('loggedUser', this.loggedUser);
      this.isloggedIn = true;
    } catch (e) {
      console.error('JWT invalide', e);
      this.clearSessionState();
    }
  }

  isTokenExpired(): Boolean {
    if (!this.token) {
      return true;
    }
    return this.helper.isTokenExpired(this.token);
  }

  isAdmin(): Boolean {
    if (!this.roles) {
      return false;
    }
    return this.roles.indexOf('ADMIN') >= 0;
  }

  logout(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('loggedUser');
    localStorage.setItem('isloggedIn', 'false');
    this.clearSessionState();
    this.router.navigate(['/login']);
  }

  // Conservee pour compatibilite avec l'atelier 16 (avant JWT)
  setLoggedUserFromLocalStorage(login: string): void {
    this.loadToken();
    if (!this.loggedUser && login) {
      this.loggedUser = login;
    }
  }

  private clearSessionState(): void {
    this.isloggedIn = false;
    this.loggedUser = undefined!;
    this.roles = undefined!;
    this.token = undefined!;
  }
}
