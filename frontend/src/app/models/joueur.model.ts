import { Auteur } from './auteur.model';
import { Equipe } from './equipe.model';

export interface Joueur {
  id?: number;
  nom: string;
  prenom: string;
  age: number;
  poste: string;
  numeroMaillot: number;
  equipe: Equipe | null;
  auteur: Auteur | null;
}
