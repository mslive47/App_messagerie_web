import { Component, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserCredentials } from '../../model/user-credentials';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { AuthenticationService } from '../../../login/services/authentication.service';

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
  loginSuccess: boolean = false;
  loginError: string | null = null;

  login = output<UserCredentials>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthenticationService
  ) {}

  /** Cette méthode permet de faire la connexion au chat */
  async onLogin() {
    if (this.loginForm.invalid) {
      return; // Empêche la soumission si le formulaire est invalide
    }

    const credentials: UserCredentials = {
      username: this.loginForm.value.username!,
      password: this.loginForm.value.password!,
    };

    this.login.emit(credentials);
    const response = await this.authService.login(credentials);

    if (response.success) {
      this.loginError = null;
      this.loginForm.reset();
      this.loginSuccess = true;
    } else {
      this.loginError = response.error || 'An unknown error occurred';
    }
  }

  /** Cette méthode permet de verifier le username */
  verifyName() {
    const usernameControl = this.loginForm.get('username');
    if (
      (usernameControl?.touched || usernameControl?.dirty) &&
      usernameControl?.invalid
    ) {
      this.checkName = true; // Afficher l'erreur si le champ est touché et invalide
      usernameControl.markAsTouched(); // Marque le champ comme touché
    } else {
      this.checkName = false; // Masquer l'erreur si le champ est valide
    }
  }

  /** Cette méthode permet de réinitialiser le champ nom d'usager */
  resetName() {
    this.checkName = false;
    this.loginForm.get('username')?.markAsUntouched(); // Réinitialise l'état du champ
    this.loginError = null;
  }

  /** Cette méthode de verifier le mot de passe */
  verifyPassWord() {
    const passwordControl = this.loginForm.get('password');
    if (
      (passwordControl?.touched || passwordControl?.dirty) &&
      passwordControl?.invalid
    ) {
      this.checkPassWord = true; // Afficher l'erreur si le champ est touché et invalide
      passwordControl.markAsTouched(); // Marque le champ comme touché
    } else {
      this.checkPassWord = false; // Masquer l'erreur si le champ est valide
    }
  }

  /** Cette méthode permet de réinitialier le champ mot de passe */
  resetPass() {
    this.checkPassWord = false;
    this.loginForm.get('password')?.markAsUntouched(); // Réinitialise l'état du champ
    this.loginError = null;
  }
}
