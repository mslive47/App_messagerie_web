import { Injectable, Signal, signal } from '@angular/core';
import { UserCredentials } from '../model/user-credentials';
import { firstValueFrom, Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { LoginResponse } from '../model/login-response';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  static KEY = 'username';
  static TOKEN_KEY = 'jwtToken';
  private username = signal<string | null>(null);
  private jwtToken = signal<string | null>(null);

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
          //{ withCredentials: true }
          { observe: 'response' } // Observe entire response to get header
        )
      );
      //console.log(loginResponse);
      const jwtToken = loginResponse.headers.get('Authorization')?.replace('Bearer ', '');
      if (jwtToken) {
        localStorage.setItem(AuthenticationService.TOKEN_KEY, jwtToken);
        localStorage.setItem(AuthenticationService.KEY, loginResponse.body?.username ?? '');
        this.jwtToken.set(jwtToken);
        this.username.set(loginResponse.body?.username ?? '');
      } 
      //localStorage.setItem(AuthenticationService.KEY, loginResponse.username);
      //this.username.set(loginResponse.username);
      return { success: true, username: loginResponse.body?.username };
    } catch (error) {
      console.error('Login failed', error);
      return { success: false, error: 'Login failed' };
    }
  }

  /* Cette methode permet de supprimer un utilisateur */
  async logout() {
    // À faire
    try {
      /*const logoutResponse = await firstValueFrom(
        this.httpClient.post<void>(
          `${environment.backendUrl}/auth/logout`,
          {},
          { withCredentials: true }
        )
      );
      localStorage.removeItem('username');
      this.username.set(null);*/
      const headers = new HttpHeaders().set('Authorization', `Bearer ${this.jwtToken()}`);
      await firstValueFrom(
        this.httpClient.post<void>(`${environment.backendUrl}/auth/logout`, {}, { headers })
      );

      localStorage.removeItem(AuthenticationService.KEY);
      localStorage.removeItem(AuthenticationService.TOKEN_KEY);
      this.username.set(null);
      this.jwtToken.set(null);
      return { success: true };
    } catch (error) {
      console.error('Logout failed', error);
      return { success: false, error: 'Logout failed' };
    }
  }

  getUsername(): Signal<string | null> {
    return this.username;
  }

  getToken(): string | null {
    return this.jwtToken();
  }

  // Helper to add Authorization header with JWT token
  getAuthHeaders(): HttpHeaders {
    return new HttpHeaders().set('Authorization', `Bearer ${this.getToken()}`);
  }
}
