import { Component, ViewChild, ElementRef, AfterViewChecked, effect, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Message } from '../../model/message.model';
import { MessagesService } from '../../services/messages.service';
import { NewMessageFormComponent } from '../new-message-form/new-message-form.component';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [DatePipe, NewMessageFormComponent],
  templateUrl: './messages.component.html',
  styleUrl: './messages.component.css'
})
export class MessagesComponent implements OnInit, AfterViewChecked {
  messages = this.messagesService.getMessages();
  username = this.authenticationService.getUsername();

  @ViewChild('chatContainer') chatContainer!: ElementRef;
  
  currentUser = this.username() ?? "";
  firstUser: string | undefined;

  constructor(
    private messagesService: MessagesService,
    private authenticationService: AuthenticationService,
  ) {
  effect(() => {
    const firstMessage = this.messagesService.getLastMessage();  
    this.firstUser = this.currentUser;  
  });
  }

  /** Afficher la date seulement si la date du message précédent est différente du message courant. */
  showDateHeader(messages: Message[] | null, i: number) {
    if (messages != null) {
      if (i === 0) {
        return true;
      } else {
        const prev = new Date(messages[i - 1].timestamp).setHours(0, 0, 0, 0);
        const curr = new Date(messages[i].timestamp).setHours(0, 0, 0, 0);
        return prev != curr;
      }
    }
    return false;
  }

  /** Cette méthode sera appelée après chaque mise à jour de la vue. */
  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  /** Cette méthode permet de faire un défilement automatique */
  scrollToBottom(): void {
    this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
  }

    /** Appeler fetchMessages() lors du chargement de la page pour afficher les messages. */
  ngOnInit(): void {
    this.messagesService.fetchMessages('') 
    .then(() => {
      console.log("Messages fetched successfully on init.");
      })
      .catch(error => {
        console.error("Error fetching messages on init:", error);
      });
  }

  /** Vérifie si le message actuel est le premier envoyé par cet utilisateur */
  firstMessageByUser(messages: Message[] | null, i: number): boolean {
    if (messages != null && i >= 0 && i < messages.length) {
      const currentUsername = messages[i].username;
      for (let j = 0; j < i; j++) {
        if (messages[j].username === currentUsername) {
          return false; 
        }
      }
      return true; 
    }
    return false;
  }

  sameUserMessage(messages: Message[] | null): boolean {
    if (messages == null || messages.length === 0) {
      return false;
    }
    const firstUsername = messages[0].username;
    return messages.every(message => message.username === firstUsername);
  }
    
}
