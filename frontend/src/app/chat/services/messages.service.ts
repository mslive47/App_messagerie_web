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
}
