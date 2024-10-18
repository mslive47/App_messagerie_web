import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Router } from '@angular/router';
import { MessagesComponent } from '../../composants/messages/messages.component';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-chat-page',
  templateUrl: './chat-page.component.html',
  styleUrls: ['./chat-page.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe, MessagesComponent, MatButtonModule],
})
export class ChatPageComponent {

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  /** Cette méthode permet de faire la deconnexion du chat */
  async onLogout() {

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

}
