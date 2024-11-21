import { Component, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { MessagesService } from '../../services/messages.service';
import { FileReaderService } from '../../services/file-reader.service';
import { ChatImageData } from '../../model/message.model';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-new-message-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatInputModule, MatIconModule],
  templateUrl: './new-message-form.component.html',
  styleUrl: './new-message-form.component.css',
})
export class NewMessageFormComponent {
  username = this.authenticationService.getUsername();
  scroll = output();
  messageId: number = 0;

  file: File | null = null;
  imageData: ChatImageData | null = null;

  messageForm = this.fb.group({
    msg: '',
  });

  constructor(
    private fb: FormBuilder,
    private messagesService: MessagesService,
    private fileReaderService: FileReaderService,
    private authenticationService: AuthenticationService
  ) {}

  /** cette methode permet d'afficher les messages envoyés */
  async onPublishMessage() {
    const usernameValue = this.username() ?? ''; // Utilise une chaîne vide si username est null

    if (
      this.username() &&
      this.messageForm.valid &&
      (this.messageForm.value.msg || this.file)
    ) {
      // const text = this.messageForm.value.msg;
      const text = this.messageForm.value.msg ?? '';
      await this.processFile(); // si y a un pb ajouter await ici et ajouter async onpublish

      const newMessage = {
        text,
        username: usernameValue,
        imageData: this.imageData,
      };
      this.messagesService.postMessage(newMessage);

      /* if (text || this.imageData) {
        this.messagesService.postMessage({
          //id : this.messageId.toString(),
          text: this.messageForm.value.msg ?? '',
          username: this.username()!,
          imageData: this.imageData,
          //timestamp: Date.now(),
        });*/
    }

    this.messageForm.reset();
    this.scroll.emit;
    this.messageId++;
    this.file = null;
    this.imageData = null;
    //this.messagesService.fetchMessages('');
  }

  get hasImage() {
    return this.file != null;
  }

  fileChanged(event: Event) {
    const input = event.target as HTMLInputElement;
    this.file = input.files ? input.files[0] : null;
  }

  async processFile() {
    if (this.file) {
      this.imageData = await this.fileReaderService.readFile(this.file);
    }
  }
}
