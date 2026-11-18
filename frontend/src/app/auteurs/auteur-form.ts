import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Auteur } from '../models/auteur.model';
import { AuteurService } from '../services/auteur.service';

@Component({
  selector: 'app-auteur-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './auteur-form.html',
  styleUrl: './auteur-form.css',
})
export class AuteurForm implements OnInit {

  auteur: Auteur = { nom: '', prenom: '', nationalite: '' };
  modeEdition = false;
  messageErreur = '';

  constructor(
    private auteurService: AuteurService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.modeEdition = true;
      this.auteurService.getAuteur(+id).subscribe({
        next: (data) => {
          this.auteur = data;
          this.cdr.markForCheck();
        },
        error: (err) => console.error(err),
      });
    }
  }

  enregistrer(): void {
    const req = this.modeEdition
      ? this.auteurService.updateAuteur(this.auteur)
      : this.auteurService.createAuteur(this.auteur);
    req.subscribe({
      next: () => this.router.navigate(['/auteurs']),
      error: (err) => {
        console.error(err);
        this.messageErreur = "Erreur lors de l'enregistrement de l'auteur : " + (err?.error?.message ?? '');
        this.cdr.markForCheck();
      },
    });
  }
}
