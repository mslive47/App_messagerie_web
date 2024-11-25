import { Component, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from 'src/app/login/services/authentication.service';
import { MessagesService } from '../../services/messages.service';
import { FileReaderService } from '../../services/file-reader.service';
import { ChatImageData } from '../../model/message.model';
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
  messageId : number = 0;

  file: File | null = null;
  imageData: ChatImageData | null = null;

  messageForm = this.fb.group({
    msg: '',
  });

  constructor(
    private fb: FormBuilder,
    private messagesService: MessagesService,
    private fileReaderService : FileReaderService,
    private authenticationService: AuthenticationService,
  ) {}

  /** cette methode permet d'afficher les messages envoyés */
  async onPublishMessage() {
    if (
      this.username() &&
      this.messageForm.valid &&
      this.messageForm.value.msg
    ) {
      const text = this.messageForm.value.msg;
      //await this.processFile();
      console.log(this.imageData?.data);
      console.log(this.imageData?.type);
      if (text || this.imageData) {
        this.messagesService.postMessage({
          text: this.messageForm.value.msg ?? '',
          username: this.username()!,
          imageData: this.imageData,
        });
      }
   
    }
    this.messageForm.reset();
    this.scroll.emit;
    this.messageId++;
    this.file = null;
    this.imageData = null;
  }

  get hasImage() {
    return this.file != null;
  }

  fileChanged(event: Event) {
    const input = event.target as HTMLInputElement;
    this.file = input.files ? input.files[0] : null;
    // Déclenche `processFile` immédiatement après le changement de fichier
    this.processFile();
  }  
  
   async processFile() {
    if (this.file) {
      this.imageData = await this.fileReaderService.readFile(this.file);
    }
  }
  
      
}
