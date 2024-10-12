import { Injectable, Signal, signal } from '@angular/core';
import { UserCredentials } from '../model/user-credentials';
import { firstValueFrom, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { LoginResponse } from '../model/login-response';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  static KEY = 'username';

  private username = signal<string | null>(null); 
  //private httpClient : HttpClient;

  constructor(private httpClient : HttpClient) {
    this.username.set(localStorage.getItem(AuthenticationService.KEY));
  }

   async login(userCredentials: UserCredentials) :  Promise<{ success: boolean; username?: string; error?: string }> {
    // À faire
    try {
      // Appel au backend avec HttpClient et firstValueFrom
      const loginResponse = await firstValueFrom(
        this.httpClient.post<LoginResponse>(
          `${environment.backendUrl}/auth/login`, // URL du backend
          userCredentials, // Données des credentials
          { withCredentials: true } // Pour envoyer et recevoir les cookies de session
        )
      );
    localStorage.setItem(AuthenticationService.KEY, loginResponse.username);
    this.username.set(loginResponse.username);
     // Simuler une réponse de succès pour la démonstration
     return { success: true }

    } catch (error) {
      // Gérer les erreurs lors de l'appel backend
      console.error('Login failed', error);
      return { success: false, error: 'Login failed' };
    }
  }

  async logout() {
    // À faire
    try {
      // Appel au backend avec HttpClient et firstValueFrom
      const logoutResponse = await firstValueFrom(
        this.httpClient.post<void>(
          `${environment.backendUrl}/auth/logout`, // URL du backend
          {},
          //userCredentials, // Données des credentials
          { withCredentials: true } // Pour envoyer et recevoir les cookies de session
        )
      );
    localStorage.removeItem('username');
    this.username.set(null);
    return { success: true }

    } catch (error) {
      // Gérer les erreurs lors de l'appel backend
      console.error('Logout failed', error);
      return { success: false, error: 'Logout failed' };
    }
  }

  getUsername(): Signal<string | null> {
    return this.username;
  }
}
