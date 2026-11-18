import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Equipe } from '../models/equipe.model';
import { EquipeWrapper } from '../models/wrappers.model';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({ providedIn: 'root' })
export class EquipeService {

  private baseUrl = `${environment.apiURL}/equipes`;

  constructor(private http: HttpClient) {}

  getAllEquipes(): Observable<Equipe[]> {
    return this.http.get<Equipe[]>(this.baseUrl);
  }

  getEquipe(id: number): Observable<Equipe> {
    return this.http.get<Equipe>(`${this.baseUrl}/${id}`);
  }

  createEquipe(equipe: Equipe): Observable<Equipe> {
    return this.http.post<Equipe>(this.baseUrl, equipe, httpOptions);
  }

  updateEquipe(equipe: Equipe): Observable<Equipe> {
    return this.http.put<Equipe>(this.baseUrl, equipe, httpOptions);
  }

  deleteEquipe(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, httpOptions);
  }

  // Atelier 09 : consommer l'API Spring Data REST (format HAL _embedded)
  listeEquipesDataRest(): Observable<EquipeWrapper> {
    return this.http.get<EquipeWrapper>(environment.restEquipesURL);
  }
}
