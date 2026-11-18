import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Joueur } from '../models/joueur.model';
import { AuthService } from '../services/auth.service';
import { JoueurService } from '../services/joueur.service';

type CritereTri = 'nom' | 'prenom' | 'poste' | 'age' | 'numeroMaillot' | 'equipe' | 'auteur';

@Component({
  selector: 'app-joueur-list',
  imports: [RouterLink, FormsModule],
  templateUrl: './joueur-list.html',
  styleUrl: './joueur-list.css',
})
export class JoueurList implements OnInit {

  joueurs: Joueur[] = [];
  allJoueurs: Joueur[] = [];
  messageErreur = '';

  // Atelier 07 : recherche (keyup local + appel API) et tri
  recherche = '';
  critereTri: CritereTri = 'nom';
  ordreTri: 'asc' | 'desc' = 'asc';

  constructor(
    private joueurService: JoueurService,
    public authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.chargerJoueurs();
  }

  chargerJoueurs(): void {
    this.joueurService.getAllJoueurs().subscribe({
      next: (data) => {
        this.joueurs = data;
        this.allJoueurs = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Impossible de charger la liste des joueurs.';
        this.cdr.markForCheck();
      },
    });
  }

  // Atelier 07 : recherche par nom en utilisant l'evenement keyup
  onKeyUp(filterText: string): void {
    const texte = (filterText ?? '').trim().toLowerCase();
    this.joueurs = !texte
      ? this.allJoueurs
      : this.allJoueurs.filter((j) =>
          (j.nom ?? '').toLowerCase().includes(texte) ||
          (j.prenom ?? '').toLowerCase().includes(texte) ||
          (j.poste ?? '').toLowerCase().includes(texte) ||
          (j.equipe?.nom ?? '').toLowerCase().includes(texte) ||
          (j.auteur?.nom ?? '').toLowerCase().includes(texte)
        );
    this.cdr.markForCheck();
  }

  // Atelier 07 : recherche par nom en utilisant une API
  rechercherParApi(): void {
    if (!this.recherche.trim()) {
      this.chargerJoueurs();
      return;
    }
    this.joueurService.rechercherParNom(this.recherche.trim()).subscribe({
      next: (data) => {
        this.joueurs = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Erreur lors de la recherche.';
        this.cdr.markForCheck();
      },
    });
  }

  // Liste reellement affichee : tri apres recherche
  get joueursAffiches(): Joueur[] {
    const facteur = this.ordreTri === 'asc' ? 1 : -1;
    return [...this.joueurs].sort((a, b) =>
      facteur * this.valeurTri(a).localeCompare(this.valeurTri(b), 'fr')
    );
  }

  private valeurTri(j: Joueur): string {
    switch (this.critereTri) {
      case 'equipe':
        return j.equipe?.nom ?? '';
      case 'auteur':
        return j.auteur?.nom ?? '';
      case 'age':
      case 'numeroMaillot':
        return String(j[this.critereTri] ?? '');
      default:
        return (j[this.critereTri] as string) ?? '';
    }
  }

  trierPar(critere: CritereTri): void {
    if (this.critereTri === critere) {
      this.ordreTri = this.ordreTri === 'asc' ? 'desc' : 'asc';
    } else {
      this.critereTri = critere;
      this.ordreTri = 'asc';
    }
  }

  indicateurTri(critere: CritereTri): string {
    if (this.critereTri !== critere) {
      return '';
    }
    return this.ordreTri === 'asc' ? ' ▲' : ' ▼';
  }

  supprimerJoueur(id: number): void {
    if (confirm('Etes-vous sûr ?')) {
      this.joueurService.deleteJoueur(id).subscribe({
        next: () => this.chargerJoueurs(),
        error: (err) => {
          console.error(err);
          this.messageErreur = 'Erreur lors de la suppression du joueur.';
          this.cdr.markForCheck();
        },
      });
    }
  }
}
