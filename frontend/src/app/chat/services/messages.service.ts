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
     const currentMessages = this.messages();
     return currentMessages.length > 0 ? currentMessages[0] : undefined;
  }
}
