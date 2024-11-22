import { CanActivateFn } from '@angular/router';
import { AuthenticationService } from '../login/services/authentication.service';
import { Router } from '@angular/router';
import { inject } from '@angular/core';

export const loginPageGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  // Vérifie si l'utilisateur est connecté
  if (authService.isConnected()) {
    // Redirige vers la page du chat si l'utilisateur est déjà connecté
    return router.parseUrl('/chat');
  }

  // Permet l'accès à la page de connexion si l'utilisateur n'est pas connecté
  return true;
};
