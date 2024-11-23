import { Component, OnDestroy, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Router } from '@angular/router';
import { MessagesComponent } from '../../composants/messages/messages.component';
import { MatButtonModule } from '@angular/material/button';
import { MessagesService } from 'src/app/chat/services/messages.service';
import { NewMessageRequest } from '../../model/message.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat-page',
  templateUrl: './chat-page.component.html',
  styleUrls: ['./chat-page.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, MessagesComponent, MatButtonModule],
})
export class ChatPageComponent implements OnInit, OnDestroy {
  private logoutSubscription: Subscription = new Subscription(); // Valeur par défaut

  constructor(
    private authenticationService: AuthenticationService,
    private messagesService: MessagesService,
    private router: Router
  ) {}

  ngOnInit() {
    // S'abonner à l'événement de déconnexion
    this.logoutSubscription = this.messagesService.logoutEvent.subscribe(() => {
      this.onLogout();
    });
  }

  ngOnDestroy() {
    // Se désabonner de l'événement lorsque le composant est détruit
    if (this.logoutSubscription) {
      this.logoutSubscription.unsubscribe();
    }
  }

  /** Cette méthode permet de faire la deconnexion du chat */
  async onLogout() {
    try {
      const response = await this.authenticationService.logout();
      if (response.success) {
        this.router.navigate(['/login']);
      } else {
        console.error('Logout failed:', response.error);
      }
    } catch (error) {
      console.error('An error occurred during login:', error);
    }
  }
}
