import { Injectable, Signal, signal } from '@angular/core';
import { Message } from '../model/message.model';

@Injectable({
  providedIn: 'root',
})
export class MessagesService {
  messages = signal<Message[]>([]);

  postMessage(message: Message): void {
    // À faire
    this.messages.set([...this.messages(), message]);
  }

  getMessages(): Signal<Message[]> {
    return this.messages;
  }

  getFirstMessage() : Message | undefined {
     // Accède à la valeur actuelle du signal (qui est un tableau)
     const currentMessages = this.messages();
    
     // Retourne le premier message s'il existe, sinon retourne 'undefined'
     return currentMessages.length > 0 ? currentMessages[0] : undefined;
  }
}
