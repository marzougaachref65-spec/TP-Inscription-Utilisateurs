import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Joueur } from '../models/joueur.model';
import { SearchFilterPipe } from '../pipes/search-filter.pipe';
import { JoueurService } from '../services/joueur.service';

// Atelier 07 : recherche par nom (API + keyup + pipe)
@Component({
  selector: 'app-recherche-par-nom',
  imports: [FormsModule, SearchFilterPipe],
  templateUrl: './recherche-par-nom.html',
  styleUrl: './recherche-par-nom.css',
})
export class RechercheParNom implements OnInit {

  nomProduit!: string;
  searchTerm = '';
  allProduits!: Joueur[];
  produits!: Joueur[];

  constructor(private produitService: JoueurService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.produitService.listeProduit().subscribe({
      next: (prods) => {
        console.log(prods);
        this.produits = prods;
        this.allProduits = prods;
        this.cdr.markForCheck();
      },
    });
  }

  // Recherche via l'API Spring Boot : GET /api/joueurs/nom/{nom}
  rechercherProds(): void {
    if (this.nomProduit) {
      this.produitService.rechercherParNom(this.nomProduit).subscribe({
        next: (prods) => {
          console.log(prods);
          this.produits = prods;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.produitService.listeProduit().subscribe({
        next: (prods) => {
          this.produits = prods;
          this.cdr.markForCheck();
        },
      });
    }
  }

  // Recherche avec l'evenement keyup
  onKeyUp(filterText: string): void {
    const texte = (filterText ?? '').toLowerCase();
    this.produits = this.allProduits.filter((item) =>
      (item.nom ?? '').toLowerCase().includes(texte)
    );
    this.cdr.markForCheck();
  }
}
