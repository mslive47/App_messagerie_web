import { Injectable, Signal, signal } from '@angular/core';
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
  lastMessageId: string = "";

  constructor(
    private httpClient: HttpClient,
    private webSocketService: WebSocketService,
    private authService: AuthenticationService //a enlever maybe
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
    try {
      const messageResponse  = await firstValueFrom(
        this.httpClient.post<Message>(
          `${environment.backendUrl}/auth/chat`,
          message,
          { headers: this.getAuthHeaders() } // Add auth headers
          //{ withCredentials: true }
        )
      );
      //console.log(messageResponse);
      this.lastMessageId = messageResponse.id;
      this.messages.set([...this.messages(), messageResponse]);
      console.log('message id envoye : ' + this.lastMessageId);
      return { success: true };
    } catch (error: any) {
      console.error('post message failed', error);
      return { success: false, error: 'post message failed' };
    }
  }

  async fetchMessages(
    id: string

  ): Promise<{ success: boolean; error?: string }> {
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
        /*const newMessages = messageResponse.filter(
          (msg) => msg.id > lastMessage.id
        );*/
        const newMessages = messageResponse.filter(
          (msg) => !currentMessages.some(existingMsg => existingMsg.id === msg.id)
        );
        this.messages.set([...currentMessages, ...newMessages]);
      } else {
        this.messages.set([...messageResponse]);
      }
      return { success: true };
    } catch (error: any) {
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
    return currentMessages.length > 0 ? currentMessages[currentMessages.length - 1] : undefined;
  }

  // Helper function to set Authorization headers
  private getAuthHeaders(): HttpHeaders {
    const jwtToken = this.authService.getToken();
    //console.log(jwtToken);
    if (!jwtToken) {
      console.error('JWT token is missing');
    }
    const header = new HttpHeaders().set('Authorization', `${jwtToken}`);
    console.log(header);
    return header;
  }
}
