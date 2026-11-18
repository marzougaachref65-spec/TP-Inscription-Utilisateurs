import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

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
    provideHttpClient(withInterceptors([tokenInterceptor]))
  ]
};
