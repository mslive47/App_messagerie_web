import { Component, effect, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Message } from '../../model/message.model';
import { MessagesService } from '../../services/messages.service';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';


@Component({
  selector: 'app-chat-page',
  templateUrl: './chat-page.component.html',
  styleUrls: ['./chat-page.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe, MatButtonModule],
})
export class ChatPageComponent {
  messages = signal<Message[]>([]);
  username = this.authenticationService.getUsername();

  messageForm = this.fb.group({
    msg: '',
  });


  constructor(
    private fb: FormBuilder,
    private messagesService: MessagesService,
    private authenticationService: AuthenticationService,
    private router: Router // Inject Router for navigation
  ) {
    // Set up effect to listen for changes in the MessagesService messages signal
    effect(() => {
      const updatedMessages = this.messagesService.getMessages();  // Get messages signal from service
      this.messages.set(updatedMessages());  // Update the local signal with the data from the service
    }, { allowSignalWrites: true });  // Enable signal writes within this effect)
  }

  onPublishMessage() {
    if (
      this.username() &&
      this.messageForm.valid &&
      this.messageForm.value.msg
    ) {
      console.log('Envoi de message :', this.messageForm.value.msg);

      this.messagesService.postMessage({
        text: this.messageForm.value.msg,
        username: this.username()!,
        timestamp: Date.now(),
      });
    }
    this.messageForm.reset();
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

  onLogout() {
    // À faire
    // Clear authentication (e.g., remove token from localStorage or session)
    this.authenticationService.logout();  
    
    // Redirect to the login page
    this.router.navigate(['/login']);
  }
}
