import { Component, ViewChild, ElementRef, AfterViewChecked, effect } from '@angular/core';
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
export class MessagesComponent implements AfterViewChecked {
  messages = this.messagesService.getMessages();
  username = this.authenticationService.getUsername();

  @ViewChild('chatContainer') chatContainer!: ElementRef;
  
  currentUser = this.username() ?? "";
  firstUser: string | undefined;

  constructor(
    private messagesService: MessagesService,
    private authenticationService: AuthenticationService,
  ) {
    // Écouter les changements des messages et mettre à jour `firstUser` dynamiquement
  effect(() => {
    const firstMessage = this.messages()[0];  // Récupère le premier message
    this.firstUser = firstMessage?.username;  // Met à jour `firstUser`
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
}
