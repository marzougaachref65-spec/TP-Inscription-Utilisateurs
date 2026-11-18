import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Joueur } from '../models/joueur.model';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({ providedIn: 'root' })
export class JoueurService {

  private baseUrl = `${environment.apiURL}/joueurs`;

  constructor(private http: HttpClient) {}

  // Atelier 05 : liste des joueurs via HttpClient
  listeProduit(): Observable<Joueur[]> {
    return this.http.get<Joueur[]>(this.baseUrl);
  }

  getAllJoueurs(): Observable<Joueur[]> {
    return this.listeProduit();
  }

  getJoueur(id: number): Observable<Joueur> {
    return this.http.get<Joueur>(`${this.baseUrl}/${id}`);
  }

  ajouterProduit(prod: Joueur): Observable<Joueur> {
    return this.http.post<Joueur>(this.baseUrl, prod, httpOptions);
  }

  createJoueur(joueur: Joueur): Observable<Joueur> {
    return this.ajouterProduit(joueur);
  }

  updateProduit(prod: Joueur): Observable<Joueur> {
    return this.http.put<Joueur>(this.baseUrl, prod, httpOptions);
  }

  updateJoueur(joueur: Joueur): Observable<Joueur> {
    return this.updateProduit(joueur);
  }

  supprimerProduit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, httpOptions);
  }

  deleteJoueur(id: number): Observable<void> {
    return this.supprimerProduit(id);
  }

  // Atelier 03 : joueurs d'une equipe donnee
  getJoueursByEquipe(idEquipe: number): Observable<Joueur[]> {
    return this.http.get<Joueur[]>(`${this.baseUrl}/equipe/${idEquipe}`);
  }

  // Nouvelle association : joueurs d'un auteur donne
  getJoueursByAuteur(idAuteur: number): Observable<Joueur[]> {
    return this.http.get<Joueur[]>(`${this.baseUrl}/auteur/${idAuteur}`);
  }

  // Atelier 07 : recherche par nom via une API
  rechercherParNom(nom: string): Observable<Joueur[]> {
    const url = `${this.baseUrl}/nom/${encodeURIComponent(nom)}`;
    return this.http.get<Joueur[]>(url);
  }

  // Atelier 07 : recherche par categorie (ici : equipe)
  rechercherParCategorie(idCat: number): Observable<Joueur[]> {
    return this.getJoueursByEquipe(idCat);
  }
}
