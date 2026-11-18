import { Pipe, PipeTransform } from '@angular/core';

// Atelier 07 : filtre cote client (pipe)
@Pipe({ name: 'searchFilter', standalone: true })
export class SearchFilterPipe implements PipeTransform {

  transform(list: any[] | null | undefined, filterText: string): any[] {
    if (!list) {
      return [];
    }
    if (!filterText) {
      return list;
    }
    const texte = filterText.toLowerCase();
    return list.filter((item) =>
      (item.nom ?? '').toLowerCase().includes(texte) ||
      (item.prenom ?? '').toLowerCase().includes(texte) ||
      (item.poste ?? '').toLowerCase().includes(texte) ||
      (item.equipe?.nom ?? '').toLowerCase().includes(texte) ||
      (item.auteur?.nom ?? '').toLowerCase().includes(texte)
    );
  }
}
