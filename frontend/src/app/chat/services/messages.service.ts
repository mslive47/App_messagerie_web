import { Injectable, Signal, signal } from '@angular/core';
import { Message } from '../model/message.model';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { firstValueFrom } from 'rxjs';
import { WebSocketService } from './web-socket.service';  // Import du WebSocketService


@Injectable({
  providedIn: 'root',
})
export class MessagesService {
  messages = signal<Message[]>([]);

  constructor(private httpClient : HttpClient, private webSocketService: WebSocketService) {
    // Connexion au WebSocket et abonnement aux notifications
    this.webSocketService.connect().subscribe({
      next: () => {
        this.fetchMessages(); // Rafraîchir les messages lorsque la notification 'notif' est reçue
      },
      error: (err) => {
        console.error('WebSocket error', err);
      },
      complete: () => {
        console.log('WebSocket connection closed');
      }
    });
  }

   async postMessage(message: Message):  Promise<{ success: boolean; error?: string }> {
    // À faire
    //this.messages.set([...this.messages(), message]); on en aura besoin dans le fetchMessage pour assigner nos resultats.
      // Appel au backend avec HttpClient et firstValueFrom
      try {
      const messageResponse = await firstValueFrom(
        this.httpClient.post<Message>(
          `${environment.backendUrl}/auth/chat`, // URL du backend à changer en /chat si ca marche pas 
          message, // Données des credentials
          { withCredentials: true } // Pour envoyer et recevoir les cookies de session
        )
      );
       // Si l'envoi est réussi, on déclenche une mise à jour des messages en appelant fetchMessages()
       //await this.fetchMessages();

     return { success: true }

    } catch (error: any) {
      // Gérer les erreurs lors de l'appel backend
      console.error('post message failed', error);
      return { success: false, error: 'post message failed' };
    }
  }

  async fetchMessages() :  Promise<{ success: boolean; error?: string }>  {
    try {
      const messageResponse = await firstValueFrom(
        this.httpClient.get<Message[]>(
          `${environment.backendUrl}/auth/chat`, // URL du backend à changer en /chat si ca marche pas 
           // Données des credentials
          { withCredentials: true } // Pour envoyer et recevoir les cookies de session
        )
      );

      this.messages.set([...messageResponse]);
  
     return { success: true }

    } catch (error: any) {
      // Gérer les erreurs lors de l'appel backend
      console.error('fetch message failed', error);
      return { success: false, error: 'fetch message failed' };
    }
  }

  getMessages(): Signal<Message[]> {
    //this.fetchMessages();
    return this.messages;
  }

  getFirstMessage() : Message | undefined {
     const currentMessages = this.messages();
     return currentMessages.length > 0 ? currentMessages[0] : undefined;
  }
}
