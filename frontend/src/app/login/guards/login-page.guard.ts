import { CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot  } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const loginPageGuard: CanActivateFn = (

  route : ActivatedRouteSnapshot,
  state : RouterStateSnapshot
) => {
    
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  if (authService.logoutState) {
    authService.resetLogoutState();
    return true;
  }

  if (authService.isconnected()) {
    return router.parseUrl('/chat');    
  } 

  return true;
};
