import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideToastr } from 'ngx-toastr';

import { routes } from './app.routes';
import { tokenInterceptor } from './services/token.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // Angular 22 est "zoneless" par defaut : les composants appellent
    // ChangeDetectorRef.markForCheck() apres chaque appel asynchrone.
    provideZonelessChangeDetection(),
    provideRouter(routes),
    // Atelier 17 : intercepteur qui ajoute le JWT a toutes les requetes HTTP.
    provideHttpClient(withInterceptors([tokenInterceptor])),
    // TP 2025 : notifications Toastr.
    provideAnimations(),
    provideToastr({
      positionClass: 'toast-top-right',
      timeOut: 4000,
      closeButton: true,
      progressBar: true,
    }),
  ]
};
