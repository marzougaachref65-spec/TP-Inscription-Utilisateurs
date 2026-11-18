import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { AuthService } from './auth.service';

// Atelier 17 : ajouter automatiquement "Authorization: Bearer <jwt>" a chaque appel.
export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const toExclude = '/login';

  // Pas de token sur la requete de login : c'est elle qui le genere.
  if (req.url.includes(toExclude)) {
    return next(req);
  }

  const jwt = authService.getToken();
  if (!jwt) {
    return next(req);
  }

  const reqWithToken = req.clone({
    setHeaders: { Authorization: `Bearer ${jwt}` },
  });
  return next(reqWithToken);
};
