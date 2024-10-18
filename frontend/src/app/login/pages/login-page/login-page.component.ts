import { Component } from '@angular/core';
import { LoginFormComponent } from '../../components/login-form/login-form.component';
import { UserCredentials } from '../../model/user-credentials';
import { AuthenticationService } from '../../services/authentication.service';
import { Router } from '@angular/router';
import { LoginResponse } from '../../model/login-response';


@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css'],
  standalone: true,
  imports: [LoginFormComponent],
})
export class LoginPageComponent {

  constructor(private authService: AuthenticationService, private router: Router) {}

  /** Cette méthode permet de faire la connexion à la page chat */
   async onLogin(userCredentials: UserCredentials) {
    try {
      const response = await this.authService.login(userCredentials);
      if (response.success) {
        console.log('Login successful, redirecting to chat page...');
        this.router.navigate(['/chat']); 
      } else {
        console.error('Login failed:', response.error);
      }
    } catch (error) {
      console.error('An error occurred during login:', error);
    }

  }
}
