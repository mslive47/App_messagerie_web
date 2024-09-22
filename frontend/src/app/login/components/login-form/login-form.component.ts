import { Component, EventEmitter, Output, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserCredentials } from '../../model/user-credentials';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
})
export class LoginFormComponent {
  loginForm = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  showInvalidFormMessage = false;

@Output() login = new EventEmitter <UserCredentials>();

  constructor(private fb: FormBuilder) {}

  onLogin() {
    // À faire
    if (this.loginForm.valid) {
      const credentials: UserCredentials = {
        username: this.loginForm.value.username!,
        password: this.loginForm.value.password!,
      };
      this.login.emit(credentials);
      this.loginForm.reset();
      this.showInvalidFormMessage = false;
    } else {
      this.showInvalidFormMessage = true;
    }
  }
}
