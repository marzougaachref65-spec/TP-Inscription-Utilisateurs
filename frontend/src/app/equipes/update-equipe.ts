import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Equipe } from '../models/equipe.model';

// Atelier 08 : transmettre les donnees entre composants avec @Input() et @Output()
@Component({
  selector: 'app-update-equipe',
  imports: [FormsModule],
  templateUrl: './update-equipe.html',
  styleUrl: './update-equipe.css',
})
export class UpdateEquipe {

  @Input()
  categorie!: Equipe;

  @Input()
  ajout!: boolean;

  @Output()
  categorieUpdated = new EventEmitter<Equipe>();

  saveCategorie(): void {
    this.categorieUpdated.emit(this.categorie);
  }
}
