// Atelier 16 + TP 2025 : utilisateur cote client (login / inscription)
export class User {
  username!: string;
  password!: string;
  roles!: string[];

  // TP 2025 : champs utilises par le formulaire d'inscription.
  email!: string;
  enabled!: boolean;
}
