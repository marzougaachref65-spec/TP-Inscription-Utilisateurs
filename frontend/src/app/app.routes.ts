import { Routes } from '@angular/router';

import { AuteurForm } from './auteurs/auteur-form';
import { AuteurList } from './auteurs/auteur-list';
import { EquipeForm } from './equipes/equipe-form';
import { EquipeJoueurs } from './equipes/equipe-joueurs';
import { EquipeList } from './equipes/equipe-list';
import { joueurGuard } from './guards/joueur.guard';
import { JoueurForm } from './joueurs/joueur-form';
import { JoueurList } from './joueurs/joueur-list';
import { Forbidden } from './forbidden/forbidden';
import { Login } from './login/login';
import { RechercheParCategorie } from './recherche/recherche-par-categorie';
import { RechercheParNom } from './recherche/recherche-par-nom';

export const routes: Routes = [
  { path: '', redirectTo: 'joueurs', pathMatch: 'full' },

  // Joueurs
  { path: 'joueurs', component: JoueurList },
  { path: 'joueurs/new', component: JoueurForm, canActivate: [joueurGuard] },
  { path: 'joueurs/edit/:id', component: JoueurForm, canActivate: [joueurGuard] },

  // Equipes
  { path: 'equipes', component: EquipeList },
  { path: 'equipes/new', component: EquipeForm, canActivate: [joueurGuard] },
  { path: 'equipes/edit/:id', component: EquipeForm, canActivate: [joueurGuard] },
  { path: 'equipes/:id/joueurs', component: EquipeJoueurs },

  // Auteurs
  { path: 'auteurs', component: AuteurList },
  { path: 'auteurs/new', component: AuteurForm, canActivate: [joueurGuard] },
  { path: 'auteurs/edit/:id', component: AuteurForm, canActivate: [joueurGuard] },

  // Atelier 07 : recherche
  { path: 'rechercheParCategorie', component: RechercheParCategorie },
  { path: 'rechercheParNom', component: RechercheParNom },

  // Atelier 16 : authentification et droits
  { path: 'login', component: Login },
  { path: 'app-forbidden', component: Forbidden },
];
