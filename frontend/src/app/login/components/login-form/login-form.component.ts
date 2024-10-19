import { Component, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserCredentials } from '../../model/user-credentials';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, MatButtonModule, MatInputModule],
})
export class LoginFormComponent {
  loginForm = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  checkName: boolean = false;
  checkPassWord: boolean = false;

  login = output<UserCredentials>();

  constructor(private fb: FormBuilder) {}

  /** Cette méthode permet de faire la connexion au chat */
  onLogin() {
    const credentials: UserCredentials = {
      username: this.loginForm.value.username!,
      password: this.loginForm.value.password!,
    };
    this.loginForm.reset();
    this.login.emit(credentials);
  }

  /** Cette méthode permet de verifier le username */
  verifyName() {
    if (this.loginForm.value.username == '') {
      this.checkName = true;
    }
  }

  /** Cette méthode permet de réinitialiser le champ nom d'usager */
  resetName() {
    this.checkName = false;
  }

  /** Cette méthode de verifier le mot de passe */
  verifyPassWord() {
    if (this.loginForm.value.password == '') {
      this.checkPassWord = true;
    }
  }

  /** Cette méthode permet de réinitialier le champ mot de passe */
  resetPass() {
    this.checkPassWord = false;
  }
}
