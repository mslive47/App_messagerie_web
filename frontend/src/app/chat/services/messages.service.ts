import { Injectable, Signal, signal } from '@angular/core';
import { Message } from '../model/message.model';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MessagesService {
  
  messages = signal<Message[]>([]);

  constructor(private httpClient: HttpClient) {}

  /**
   * Récupère les messages du backend
   */
  async fetchMessages(): Promise<Message[]> {
    try {
      // Appel au backend pour récupérer les messages
      const messages = await firstValueFrom(
        this.httpClient.get<Message[]>(`${environment.backendUrl}/messages`)
      );
      this.messages.set(messages); // Use `set()` to update the signal
      return messages;
    } catch (error) {
      console.error('Failed to fetch messages', error);
      return [];
    }
  }

  /**
   * Publie un nouveau message
   * @param message 
   */
  async postMessage(message: Message): Promise<Message | null> {
    try {
      // Appel au backend pour poster un nouveau message
      const createdMessage = await firstValueFrom(
        this.httpClient.post<Message>(`${environment.backendUrl}/messages`, message)
      );
      await this.fetchMessages(); // Reload messages after posting
      return createdMessage;
    } catch (error) {
      console.error('Failed to post message', error);
      return null;
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
