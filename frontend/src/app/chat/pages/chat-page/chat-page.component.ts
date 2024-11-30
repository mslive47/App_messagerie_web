import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Router } from '@angular/router';
import { MessagesComponent } from '../../composants/messages/messages.component';
import { MatButtonModule } from '@angular/material/button';
import { MessagesService } from '../../services/messages.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat-page',
  templateUrl: './chat-page.component.html',
  styleUrls: ['./chat-page.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, MessagesComponent, MatButtonModule],
})
export class ChatPageComponent {
  private unauthorizedSubscription: Subscription;
  
  constructor(
    private messagesService: MessagesService,
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
    this.unauthorizedSubscription = this.messagesService.unauthorized.subscribe(() => {
      this.handleLogout();
    });
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

  async handleLogout() {
    await this.onLogout();
  }

}
