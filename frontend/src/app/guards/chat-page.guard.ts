import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../login/services/authentication.service';

export const chatPageGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  // Vérifie si l'utilisateur est connecté
  if (!authService.isConnected()) {
    // Redirige vers la page de connexion si non connecté
    return router.parseUrl('/login');
  }

  // Autorise l'accès si connecté
  return true;
};
