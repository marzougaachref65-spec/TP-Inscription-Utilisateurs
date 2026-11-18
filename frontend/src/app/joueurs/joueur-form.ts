import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Auteur } from '../models/auteur.model';
import { Equipe } from '../models/equipe.model';
import { Joueur } from '../models/joueur.model';
import { AuteurService } from '../services/auteur.service';
import { EquipeService } from '../services/equipe.service';
import { JoueurService } from '../services/joueur.service';

@Component({
  selector: 'app-joueur-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './joueur-form.html',
  styleUrl: './joueur-form.css',
})
export class JoueurForm implements OnInit {

  joueur: Joueur = {
    nom: '',
    prenom: '',
    age: 18,
    poste: '',
    numeroMaillot: 1,
    equipe: null,
    auteur: null,
  };

  equipes: Equipe[] = [];
  auteurs: Auteur[] = [];
  equipeId: number | null = null;
  auteurId: number | null = null;
  modeEdition = false;
  messageErreur = '';

  constructor(
    private joueurService: JoueurService,
    private equipeService: EquipeService,
    private auteurService: AuteurService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Listes deroulantes (Atelier 04 : ajout de la categorie au produit)
    this.equipeService.getAllEquipes().subscribe({
      next: (data) => {
        this.equipes = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error(err),
    });

    this.auteurService.listeAuteurs().subscribe({
      next: (data) => {
        this.auteurs = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error(err),
    });

    // Atelier 03 : transmission de parametres avec ActivatedRoute
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.modeEdition = true;
      this.joueurService.getJoueur(+id).subscribe({
        next: (data) => {
          this.joueur = data;
          this.equipeId = data.equipe ? (data.equipe.id ?? null) : null;
          this.auteurId = data.auteur ? (data.auteur.id ?? null) : null;
          this.cdr.markForCheck();
        },
        error: (err) => console.error(err),
      });
    }
  }

  enregistrer(): void {
    // Atelier 04 : retrouver l'entite choisie dans la liste deroulante
    this.joueur.equipe = this.equipes.find((e) => e.id === this.equipeId) ?? null;
    this.joueur.auteur = this.auteurs.find((a) => a.id === this.auteurId) ?? null;

    if (this.modeEdition) {
      this.joueurService.updateJoueur(this.joueur).subscribe({
        next: () => this.router.navigate(['/joueurs']),
        error: (err) => {
          console.error(err);
          this.messageErreur = "Erreur lors de la modification du joueur : " + this.lireErreur(err);
          this.cdr.markForCheck();
        },
      });
    } else {
      this.joueurService.createJoueur(this.joueur).subscribe({
        next: () => this.router.navigate(['/joueurs']),
        error: (err) => {
          console.error(err);
          this.messageErreur = "Erreur lors de l'ajout du joueur : " + this.lireErreur(err);
          this.cdr.markForCheck();
        },
      });
    }
  }

  private lireErreur(err: any): string {
    return err?.error?.message ?? err?.message ?? 'erreur inconnue';
  }
}
