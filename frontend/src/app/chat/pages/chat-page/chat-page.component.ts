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
  onLogout() {
     this.authenticationService.logout();  
     this.router.navigate(['/login']);
 
  }

}
