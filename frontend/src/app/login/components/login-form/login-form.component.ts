import { Component, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserCredentials } from '../../model/user-credentials';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { AuthenticationService } from '../../../login/services/authentication.service';
import { HttpErrorResponse } from '@angular/common/http';

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
  loginSuccess : boolean = false;
  loginError: string | null = null;

  login = output<UserCredentials>();

  constructor(private fb: FormBuilder, private authService: AuthenticationService) {}

  /** Cette méthode permet de faire la connexion au chat */
  async onLogin() {
    const credentials: UserCredentials = {
      username: this.loginForm.value.username!,
      password: this.loginForm.value.password!,
    };

    this.login.emit(credentials)
    try {
      const response = await this.authService.login(credentials);

      if (response.success) {
        this.loginError = null;
        this.loginForm.reset();
        this.loginSuccess = true;
      } //else {
        //this.loginError = response.error || 'An unknown error occurred';
     // }
    } catch (error) {
      //console.log('MEssage: ${error.status === 403}');
      if (error instanceof HttpErrorResponse) {
        if (error.status === 403) {
          this.loginError = 'Mot de passe invalide';
        } else {
          this.loginError = 'Problème de connexion';
        }
      } else {
        this.loginError = 'Une erreur inattendue est survenue';
      }
    }

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
    this.loginError =null;
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
    this.loginError =null;
  }
}
