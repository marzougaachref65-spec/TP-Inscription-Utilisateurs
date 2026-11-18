import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Auteur } from '../models/auteur.model';
import { AuteurWrapper } from '../models/wrappers.model';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({ providedIn: 'root' })
export class AuteurService {

  private baseUrl = `${environment.apiURL}/auteurs`;

  constructor(private http: HttpClient) {}

  listeAuteurs(): Observable<Auteur[]> {
    return this.http.get<Auteur[]>(this.baseUrl);
  }

  getAllAuteurs(): Observable<Auteur[]> {
    return this.listeAuteurs();
  }

  getAuteur(id: number): Observable<Auteur> {
    return this.http.get<Auteur>(`${this.baseUrl}/${id}`);
  }

  ajouterAuteur(auteur: Auteur): Observable<Auteur> {
    return this.http.post<Auteur>(this.baseUrl, auteur, httpOptions);
  }

  createAuteur(auteur: Auteur): Observable<Auteur> {
    return this.ajouterAuteur(auteur);
  }

  updateAuteur(auteur: Auteur): Observable<Auteur> {
    return this.http.put<Auteur>(this.baseUrl, auteur, httpOptions);
  }

  supprimerAuteur(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, httpOptions);
  }

  deleteAuteur(id: number): Observable<void> {
    return this.supprimerAuteur(id);
  }

  // Atelier 09 : Spring Data REST : GET /restAuteurs
  listeAuteursDataRest(): Observable<AuteurWrapper> {
    return this.http.get<AuteurWrapper>(environment.restAuteursURL);
  }
}
