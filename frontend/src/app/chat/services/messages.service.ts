import { EventEmitter, Injectable, Signal, signal } from '@angular/core';
import { Message, NewMessageRequest } from '../model/message.model';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { firstValueFrom } from 'rxjs';
import { WebSocketService } from './web-socket.service';
import { AuthenticationService } from 'src/app/login/services/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class MessagesService {
  messages = signal<Message[]>([]);
  lastMessageId: string = '';
  // Déclare un EventEmitter pour émettre un événement de déconnexion
  logoutEvent = new EventEmitter<void>();

  constructor(
    private httpClient: HttpClient,
    private webSocketService: WebSocketService,
    private authService: AuthenticationService
  ) {
    // Connexion au WebSocket et abonnement aux notifications
    this.webSocketService.connect().subscribe({
      next: () => {
        this.fetchMessages(this.lastMessageId);
      },
      error: (err) => {
        console.error('WebSocket error', err);
      },
      complete: () => {
        console.log('WebSocket connection closed');
      },
    });
  }

  async postMessage(
    message: NewMessageRequest
  ): Promise<{ success: boolean; error?: string }> {
    // À faire
    const token = this.authService.getToken();
    console.log('Vérification du token :', token);

    // Vérifier si le token existe avant de faire la requête
    if (!token) {
      console.warn('Token manquant, session expirée.');
      this.handleSessionExpired();
      return { success: false, error: 'Session expirée' };
    }

    try {
      const messageResponse = await firstValueFrom(
        this.httpClient.post<Message>(
          `${environment.backendUrl}/auth/chat`,
          message,
          { headers: this.getAuthHeaders() }
        )
      );
      this.lastMessageId = messageResponse.id;
      this.messages.set([...this.messages(), messageResponse]);
      console.log('message id envoye : ' + this.lastMessageId);
      return { success: true };
    } catch (error: any) {
      if (error?.status === 403) {
        this.handleSessionExpired();
      }

      console.error('post message failed', error);

      // Ajouter le message dans la file d’attente WebSocket
      this.webSocketService.queueMessage(message);
      return { success: false, error: 'post message failed' };
    }
  }

  async fetchMessages(
    id: string
  ): Promise<{ success: boolean; error?: string }> {
    // Vérifier si le token existe avant de faire la requête
    if (!this.authService.getToken()) {
      this.handleSessionExpired();
      return { success: false, error: 'Session expirée' };
    }

    try {
      const url = id
        ? `${environment.backendUrl}/auth/chat?fromId=${id}`
        : `${environment.backendUrl}/auth/chat`;
      const messageResponse = await firstValueFrom(
        this.httpClient.get<Message[]>(url, { headers: this.getAuthHeaders() })
      );
      const currentMessages = this.messages();
      if (currentMessages.length > 0) {
        const lastMessage = currentMessages[currentMessages.length - 1];
        const newMessages = messageResponse.filter(
          (msg) =>
            !currentMessages.some((existingMsg) => existingMsg.id === msg.id)
        );
        this.messages.set([...currentMessages, ...newMessages]);
      } else {
        this.messages.set([...messageResponse]);
      }
      return { success: true };
    } catch (error: any) {
      if (error?.status === 403) {
        this.handleSessionExpired();
      }

      console.error('fetch message failed', error);
      return { success: false, error: 'fetch message failed' };
    }
  }

  getMessages(): Signal<Message[]> {
    return this.messages;
  }

  getFirstMessage(): Message | undefined {
    const currentMessages = this.messages();
    return currentMessages.length > 0 ? currentMessages[0] : undefined;
  }

  getLastMessage(): Message | undefined {
    const currentMessages = this.messages();
    return currentMessages.length > 0
      ? currentMessages[currentMessages.length - 1]
      : undefined;
  }

  private getAuthHeaders(): HttpHeaders {
    const jwtToken = this.authService.getToken();
    if (!jwtToken) {
      console.error('JWT token is missing');
    }
    const header = new HttpHeaders().set('Authorization', `${jwtToken}`);
    return header;
  }

  private handleSessionExpired() {
    console.warn('Session expirée. Redirection vers la page de connexion.');
    // Émettre l'événement de déconnexion
    this.logoutEvent.emit();
  }
}
