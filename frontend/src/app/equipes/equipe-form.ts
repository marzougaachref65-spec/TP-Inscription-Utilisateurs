import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Equipe } from '../models/equipe.model';
import { EquipeService } from '../services/equipe.service';

@Component({
  selector: 'app-equipe-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './equipe-form.html',
  styleUrl: './equipe-form.css',
})
export class EquipeForm implements OnInit {

  equipe: Equipe = { nom: '', ville: '', pays: '', dateCreation: '' };
  modeEdition = false;
  messageErreur = '';

  constructor(
    private equipeService: EquipeService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.modeEdition = true;
      this.equipeService.getEquipe(+id).subscribe({
        next: (data) => {
          this.equipe = data;
          this.equipe.dateCreation = data.dateCreation ? data.dateCreation.substring(0, 10) : '';
          this.cdr.markForCheck();
        },
        error: (err) => console.error(err),
      });
    }
  }

  enregistrer(): void {
    const req = this.modeEdition
      ? this.equipeService.updateEquipe(this.equipe)
      : this.equipeService.createEquipe(this.equipe);
    req.subscribe({
      next: () => this.router.navigate(['/equipes']),
      error: (err) => {
        console.error(err);
        this.messageErreur = "Erreur lors de l'enregistrement de l'équipe : " + (err?.error?.message ?? '');
        this.cdr.markForCheck();
      },
    });
  }
}
