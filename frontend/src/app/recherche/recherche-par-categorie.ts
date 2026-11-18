import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Equipe } from '../models/equipe.model';
import { Joueur } from '../models/joueur.model';
import { EquipeService } from '../services/equipe.service';
import { JoueurService } from '../services/joueur.service';

// Atelier 07 : recherche des joueurs par categorie (ici : equipe)
@Component({
  selector: 'app-recherche-par-categorie',
  imports: [FormsModule],
  templateUrl: './recherche-par-categorie.html',
  styleUrl: './recherche-par-categorie.css',
})
export class RechercheParCategorie implements OnInit {

  produits!: Joueur[];
  IdCategorie!: number;
  categories!: Equipe[];
  messageErreur = '';

  constructor(
    private produitService: JoueurService,
    private equipeService: EquipeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Atelier 09 : liste des categories via Spring Data REST
    this.equipeService.listeEquipesDataRest().subscribe({
      next: (cats) => {
        this.categories = cats._embedded.equipes;
        this.cdr.markForCheck();
      },
      error: (err) => console.error(err),
    });
  }

  onChange(): void {
    this.produitService.rechercherParCategorie(this.IdCategorie).subscribe({
      next: (prods) => {
        this.produits = prods;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Erreur lors de la recherche par équipe.';
        this.cdr.markForCheck();
      },
    });
  }
}
