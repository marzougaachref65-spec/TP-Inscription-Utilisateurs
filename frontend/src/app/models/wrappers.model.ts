import { Auteur } from './auteur.model';
import { Equipe } from './equipe.model';

// Atelier 09 : format HAL renvoye par Spring Data REST
export interface EquipeWrapper {
  _embedded: { equipes: Equipe[] };
}

export interface AuteurWrapper {
  _embedded: { auteurs: Auteur[] };
}
