import { Injectable, Signal, signal } from '@angular/core';
import { Message } from '../model/message.model';

@Injectable({
  providedIn: 'root',
})
export class MessagesService {
  messages = signal<Message[]>([]);

  postMessage(message: Message): void {
    // À faire
     // Update the signal by adding the new message to the array
     const currentMessages = this.messages(); // Get current messages
     this.messages.set([...currentMessages, message]); // Add the new message

     // Afficher les messages mis à jour dans la console
  console.log('Messages après l\'ajout :', this.messages());
  }

  getMessages(): Signal<Message[]> {
    return this.messages;
  }
}
