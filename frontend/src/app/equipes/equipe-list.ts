import { DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Equipe } from '../models/equipe.model';
import { AuthService } from '../services/auth.service';
import { EquipeService } from '../services/equipe.service';
import { UpdateEquipe } from './update-equipe';

type CritereTri = 'nom' | 'ville' | 'pays' | 'dateCreation';

@Component({
  selector: 'app-equipe-list',
  imports: [RouterLink, DatePipe, FormsModule, UpdateEquipe],
  templateUrl: './equipe-list.html',
  styleUrl: './equipe-list.css',
})
export class EquipeList implements OnInit {

  equipes: Equipe[] = [];
  messageErreur = '';

  recherche = '';
  critereTri: CritereTri = 'nom';
  ordreTri: 'asc' | 'desc' = 'asc';

  // Atelier 08 : @Input / @Output avec le composant enfant UpdateEquipe
  updatedCat: Equipe = { nom: '', ville: '', pays: '', dateCreation: '' };
  ajout = true;

  constructor(
    private equipeService: EquipeService,
    public authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.chargerEquipes();
  }

  // Atelier 09 : consommation de l'API Spring Data REST (HAL _embedded)
  chargerEquipes(): void {
    this.equipeService.listeEquipesDataRest().subscribe({
      next: (data) => {
        this.equipes = data?._embedded?.equipes ?? [];
        this.cdr.markForCheck();
      },
      error: () => {
        // Repli sur le controleur REST classique si Spring Data REST est indisponible
        this.equipeService.getAllEquipes().subscribe({
          next: (data) => {
            this.equipes = data;
            this.cdr.markForCheck();
          },
          error: (err) => {
            console.error(err);
            this.messageErreur = 'Impossible de charger la liste des équipes.';
            this.cdr.markForCheck();
          },
        });
      },
    });
  }

  get equipesAffichees(): Equipe[] {
    const terme = this.recherche.trim().toLowerCase();
    const resultat = this.equipes.filter((e) =>
      !terme ||
      (e.nom ?? '').toLowerCase().includes(terme) ||
      (e.ville ?? '').toLowerCase().includes(terme) ||
      (e.pays ?? '').toLowerCase().includes(terme)
    );

    const facteur = this.ordreTri === 'asc' ? 1 : -1;
    return resultat.sort((a, b) => facteur * this.comparer(a, b));
  }

  private comparer(a: Equipe, b: Equipe): number {
    if (this.critereTri === 'dateCreation') {
      const da = a.dateCreation ? new Date(a.dateCreation).getTime() : 0;
      const db = b.dateCreation ? new Date(b.dateCreation).getTime() : 0;
      return da - db;
    }
    return ((a[this.critereTri] as string) ?? '').localeCompare((b[this.critereTri] as string) ?? '', 'fr');
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

  // Evenement emis par le composant enfant (Atelier 08)
  categorieUpdated(cat: Equipe): void {
    console.log('Cat updated event', cat);
    const req = cat.id
      ? this.equipeService.updateEquipe(cat)
      : this.equipeService.createEquipe(cat);
    req.subscribe({
      next: () => {
        this.nouvelleEquipe();
        this.chargerEquipes();
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Erreur lors de l’enregistrement de l’équipe : ' + (err?.error?.message ?? '');
        this.cdr.markForCheck();
      },
    });
  }

  updateCat(cat: Equipe): void {
    this.updatedCat = { ...cat };
    this.ajout = false;
    this.cdr.markForCheck();
  }

  nouvelleEquipe(): void {
    this.updatedCat = { nom: '', ville: '', pays: '', dateCreation: '' };
    this.ajout = true;
    this.cdr.markForCheck();
  }

  supprimerEquipe(id: number): void {
    if (confirm('Etes-vous sûr ?')) {
      this.equipeService.deleteEquipe(id).subscribe({
        next: () => this.chargerEquipes(),
        error: (err) => {
          console.error(err);
          this.messageErreur = 'Suppression impossible : cette équipe contient encore des joueurs.';
          this.cdr.markForCheck();
        },
      });
    }
  }
}
