<<<<<<< HEAD
import { Component, effect, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Message } from '../../model/message.model';
import { MessagesService } from '../../services/messages.service';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';

=======
import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Router } from '@angular/router';
import { MessagesComponent } from '../../composants/messages/messages.component';
import { MatButtonModule } from '@angular/material/button';
>>>>>>> dev_ms

@Component({
  selector: 'app-chat-page',
  templateUrl: './chat-page.component.html',
  styleUrls: ['./chat-page.component.css'],
  standalone: true,
<<<<<<< HEAD
  imports: [ReactiveFormsModule, DatePipe, MatButtonModule, MatFormFieldModule, MatIconModule],
=======
  imports: [ReactiveFormsModule, DatePipe, MessagesComponent, MatButtonModule],
>>>>>>> dev_ms
})
export class ChatPageComponent {


  constructor(
<<<<<<< HEAD
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
=======
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  /** Cette méthode permet de faire la deconnexion du chat */
  async onLogout() {
>>>>>>> dev_ms

    try {
      const response = await this.authenticationService.logout();
      if (response.success) {
        // Rediriger vers la page de login après un logout réussi
        this.router.navigate(['/login']);
      } else {
        console.error('Logout failed:', response.error);
      }
    } catch (error) {
      console.error('An error occurred during login:', error);
    }
    // this.authenticationService.logout();  
    // this.router.navigate(['/login']);
 
  }

<<<<<<< HEAD
  onLogout() {
    // À faire
    // Clear authentication (e.g., remove token from localStorage or session)
    this.authenticationService.logout();  
    
    // Redirect to the login page
    this.router.navigate(['/login']);
  }
=======
>>>>>>> dev_ms
}
