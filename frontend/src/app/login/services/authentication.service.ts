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

  constructor(private httpClient : HttpClient) {
    this.username.set(localStorage.getItem(AuthenticationService.KEY));
  }

  /* Cette methode permet d'enregistrer un utilisateur */
   async login(userCredentials: UserCredentials) :  Promise<{ success: boolean; username?: string; error?: string }> {
    // À faire
    try {
      const loginResponse = await firstValueFrom(
        this.httpClient.post<LoginResponse>(
          `${environment.backendUrl}/auth/login`, 
          userCredentials,
          { withCredentials: true } 
        )
      );
    localStorage.setItem(AuthenticationService.KEY, loginResponse.username);
    this.username.set(loginResponse.username);

     return { success: true }

    } catch (error) {
      console.error('Login failed', error);
      return { success: false, error: 'Login failed' };
    }
  }

  /* Cette methode permet de supprimer un utilisateur */
  async logout() {
    // À faire
    try {
      const logoutResponse = await firstValueFrom(
        this.httpClient.post<void>(
          `${environment.backendUrl}/auth/logout`, 
          {},
          { withCredentials: true } 
        )
      );
    localStorage.removeItem('username');
    this.username.set(null);
    return { success: true }

    } catch (error) {
      console.error('Logout failed', error);
      return { success: false, error: 'Logout failed' };
    }
  }

  getUsername(): Signal<string | null> {
    return this.username;
  }
}
