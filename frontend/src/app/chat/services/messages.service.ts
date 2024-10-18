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
  lastMessageId : number = 0;

  constructor(private httpClient : HttpClient, private webSocketService: WebSocketService) {
    this.webSocketService.connect().subscribe({
      next: () => {
        this.fetchMessages(this.lastMessageId); 
      },
      error: (err) => {
        console.error('WebSocket error', err);
      },
      complete: () => {
        console.log('WebSocket connection closed');
      }
    });
  }

    /* Cette methode permet de publier un message */
    async postMessage(message: Message):  Promise<{ success: boolean; error?: string }> {
    // À faire
      try {
      const messageResponse = await firstValueFrom(
        this.httpClient.post<Message>(
          `${environment.backendUrl}/auth/chat`,  
          message,
          { withCredentials: true }
        )
      );
       this.lastMessageId = messageResponse.id;
       console.log('message id envoye : '  + this.lastMessageId);

     return { success: true }

    } catch (error: any) {
      console.error('post message failed', error);
      return { success: false, error: 'post message failed' };
    }
  }

  /* Cette methode permet d'obtenir les message sauvegardés */
  async fetchMessages(id : number) :  Promise<{ success: boolean; error?: string }>  {
    try {
      const url = id ? `${environment.backendUrl}/auth/chat?fromId=${id}` : `${environment.backendUrl}/auth/chat`;
      const messageResponse = await firstValueFrom(
        this.httpClient.get<Message[]>(
          url,
          { withCredentials: true } 
        )
      );

    const currentMessages = this.messages();
    
    if (currentMessages.length > 0) {
      const lastMessage = currentMessages[currentMessages.length - 1];
      const newMessages = messageResponse.filter(msg => msg.id > lastMessage.id);
      this.messages.set([...currentMessages, ...newMessages]);
    } else {
      this.messages.set([...messageResponse]);
    }
  
     return { success: true }

    } catch (error: any) {
      console.error('fetch message failed', error);
      return { success: false, error: 'fetch message failed' };
    }
  }

  getMessages(): Signal<Message[]> {
    return this.messages;
  }

  getFirstMessage() : Message | undefined {
     const currentMessages = this.messages();
     return currentMessages.length > 0 ? currentMessages[0] : undefined;
  }
}
