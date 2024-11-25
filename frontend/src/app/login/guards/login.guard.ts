import { CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const loginGuard: CanActivateFn = (
  route : ActivatedRouteSnapshot,
  state : RouterStateSnapshot) => {
  
  const authService = inject(AuthenticationService);
  const router = inject(Router);
  //const connection = authService.isconnected();

  if (!authService.isconnected()) {
    return router.parseUrl('/login');
    //return router.parseUrl('/chat');    
  } else {
    return true;
  }


  /*if(authService.isconnected()) {
    return router.parseUrl('/chat');
  }  else {
    //trouver un moyen d'afficher  un message d'erreur 
    return true; // redirection vers la page de connexion 
  }*/
};
