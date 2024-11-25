import { Injectable, Signal, signal } from '@angular/core';
import { UserCredentials } from '../model/user-credentials';
import { firstValueFrom, Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { LoginResponse } from '../model/login-response';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  static KEY = 'username';
  static TOKEN_KEY = 'jwtToken';
  private username = signal<string | null>(null);
  private jwtToken = signal<string | null>(null);
  logoutState : boolean = false;

  constructor(private httpClient: HttpClient) {
    this.username.set(localStorage.getItem(AuthenticationService.KEY));
    this.jwtToken.set(localStorage.getItem(AuthenticationService.TOKEN_KEY));
  }

  async login(
    userCredentials: UserCredentials
  ): Promise<{ success: boolean; username?: string; error?: string }> {
    // À faire
    try {
      const loginResponse = await firstValueFrom(
        this.httpClient.post<LoginResponse>(
          `${environment.backendUrl}/auth/login`,
          userCredentials,
          { observe: 'response' } 
        )
      );
      //console.log(loginResponse);
      const jwtToken = loginResponse.headers.get('Authorization')?.replace('Bearer ', '');
      if (jwtToken) {
        localStorage.setItem(AuthenticationService.TOKEN_KEY, jwtToken);
        localStorage.setItem(AuthenticationService.KEY, loginResponse.body?.username ?? '');
        this.jwtToken.set(jwtToken);
        this.username.set(loginResponse.body?.username ?? '');
        this.logoutState = false;
      } 

      return { success: true, username: loginResponse.body?.username };
    } catch (error) {
      if (error instanceof HttpErrorResponse) {
        throw error;
      }
      console.error('Login failed', error);
      return { success: false, error: 'Login failed' };
    }
  }

  /* Cette methode permet de supprimer un utilisateur */
  async logout() {
    // À faire
    try {
      const headers = new HttpHeaders().set('Authorization', `Bearer${this.jwtToken()}`);
      await firstValueFrom(
        this.httpClient.post<void>(`${environment.backendUrl}/auth/logout`, 
          {}, { headers})
      );

      localStorage.removeItem(AuthenticationService.KEY);
      localStorage.removeItem(AuthenticationService.TOKEN_KEY);
      this.username.set(null);
      this.jwtToken.set(null);
      this.logoutState = true; //a enlever peut etre
      return { success: true };
    } catch (error) {
      console.error('Logout failed', error);
      return { success: false, error: 'Logout failed' };
    }
  }

  isconnected () {
    const user = localStorage.getItem('username');
    const connect : boolean = user != null;
    return connect;
  }

  getUsername(): Signal<string | null> {
    return this.username;
  }

  getToken(): string | null {
    return this.jwtToken();
  }

  
  getAuthHeaders(): HttpHeaders {
    return new HttpHeaders().set('Authorization', `Bearer${this.getToken()}`);
  }

  resetLogoutState() {
    this.logoutState = false; // Réinitialise l'état de déconnexion.
  }
}
