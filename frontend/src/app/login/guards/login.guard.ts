import { CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const loginGuard: CanActivateFn = (
  route : ActivatedRouteSnapshot,
  state : RouterStateSnapshot) => {
  
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  if (!authService.isconnected()) {
    return router.parseUrl('/login');   
  } else {
    return true;
  }

};
