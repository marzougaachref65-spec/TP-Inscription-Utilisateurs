import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { AuthService } from './auth.service';

// Atelier 17 + TP 2025 : ajouter "Authorization: Bearer <jwt>" sauf sur
// les endpoints publics (login, inscription, verification de l'email).
const excludeArray: string[] = ['/login', '/register', '/verifyEmail'];

function toExclude(url: string): boolean {
  return excludeArray.some((fragment) => url.includes(fragment));
}

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (toExclude(req.url)) {
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
