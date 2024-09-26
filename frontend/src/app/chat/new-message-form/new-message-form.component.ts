import { Component, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { Message } from '../model/message.model';
import { MessagesService } from '../services/messages.service';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-new-message-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatInputModule],
  templateUrl: './new-message-form.component.html',
  styleUrl: './new-message-form.component.css'
})
export class NewMessageFormComponent {

  //messages = this.messagesService.getMessages();
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
