import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Equipe } from '../models/equipe.model';
import { Joueur } from '../models/joueur.model';
import { EquipeService } from '../services/equipe.service';
import { JoueurService } from '../services/joueur.service';

@Component({
  selector: 'app-equipe-joueurs',
  imports: [RouterLink],
  templateUrl: './equipe-joueurs.html',
  styleUrl: './equipe-joueurs.css',
})
export class EquipeJoueurs implements OnInit {

  equipe: Equipe | null = null;
  joueurs: Joueur[] = [];
  messageErreur = '';

  constructor(
    private joueurService: JoueurService,
    private equipeService: EquipeService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = +this.route.snapshot.params['id'];

    this.equipeService.getEquipe(id).subscribe({
      next: (data) => {
        this.equipe = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Équipe introuvable.';
        this.cdr.markForCheck();
      },
    });

    // Atelier 03 : GET /api/joueurs/equipe/{idEquipe}
    this.joueurService.getJoueursByEquipe(id).subscribe({
      next: (data) => {
        this.joueurs = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Impossible de charger les joueurs de cette équipe.';
        this.cdr.markForCheck();
      },
    });
  }
}
