import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

// Atelier 16 : seul un ADMIN peut ajouter / modifier des joueurs
export const joueurGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true;
  }
  router.navigate(['app-forbidden']);
  return false;
};
