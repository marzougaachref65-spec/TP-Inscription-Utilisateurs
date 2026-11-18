import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Auteur } from '../models/auteur.model';
import { SearchFilterPipe } from '../pipes/search-filter.pipe';
import { AuthService } from '../services/auth.service';
import { AuteurService } from '../services/auteur.service';

@Component({
  selector: 'app-auteur-list',
  imports: [RouterLink, FormsModule, SearchFilterPipe],
  templateUrl: './auteur-list.html',
  styleUrl: './auteur-list.css',
})
export class AuteurList implements OnInit {

  auteurs: Auteur[] = [];
  recherche = '';
  messageErreur = '';

  constructor(
    private auteurService: AuteurService,
    public authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.chargerAuteurs();
  }

  chargerAuteurs(): void {
    this.auteurService.listeAuteurs().subscribe({
      next: (data) => {
        this.auteurs = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Impossible de charger la liste des auteurs.';
        this.cdr.markForCheck();
      },
    });
  }

  supprimerAuteur(id: number): void {
    if (confirm('Etes-vous sûr ?')) {
      this.auteurService.deleteAuteur(id).subscribe({
        next: () => this.chargerAuteurs(),
        error: (err) => {
          console.error(err);
          this.messageErreur = 'Suppression impossible : cet auteur a encore des joueurs.';
          this.cdr.markForCheck();
        },
      });
    }
  }
}
