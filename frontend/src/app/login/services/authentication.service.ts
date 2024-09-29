import { Injectable, Signal, signal } from '@angular/core';
import { UserCredentials } from '../model/user-credentials';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  static KEY = 'username';

  private username = signal<string | null>(null);

  constructor() {
    this.username.set(localStorage.getItem(AuthenticationService.KEY));
  }

  login(userCredentials: UserCredentials) {
    // À faire
    localStorage.setItem(AuthenticationService.KEY, userCredentials.username);
    this.username.set(userCredentials.username);
     // Simuler une réponse de succès pour la démonstration
     return of({ success: true })
  }

  logout() {
    // À faire
    localStorage.removeItem('username');
    this.username.set(null);
  }

  getUsername(): Signal<string | null> {
    return this.username;
  }
}
