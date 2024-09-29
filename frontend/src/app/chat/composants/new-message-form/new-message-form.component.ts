import { Component, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { MessagesService } from '../../services/messages.service';
import { MatInputModule } from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon'

@Component({
  selector: 'app-new-message-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatInputModule, MatIconModule],
  templateUrl: './new-message-form.component.html',
  styleUrl: './new-message-form.component.css'
})
export class NewMessageFormComponent {

  username = this.authenticationService.getUsername();
  scroll = output();

  messageForm = this.fb.group({
    msg: '',
  });

  constructor(
    private fb: FormBuilder,
    private messagesService: MessagesService,
    private authenticationService: AuthenticationService,
  ) {}

  /** cette methode permet d'afficher les messages envoyés */
  onPublishMessage() {
    if (
      this.username() &&
      this.messageForm.valid &&
      this.messageForm.value.msg
    ) {
      this.messagesService.postMessage({
        text: this.messageForm.value.msg,
        username: this.username()!,
        timestamp: Date.now(),
      });
    }
    this.messageForm.reset();
    this.scroll.emit;
  }

}
