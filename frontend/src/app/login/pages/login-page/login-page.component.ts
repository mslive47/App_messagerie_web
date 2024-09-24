import { Component } from '@angular/core';
import { LoginFormComponent } from '../../components/login-form/login-form.component';
import { UserCredentials } from '../../model/user-credentials';
import { AuthenticationService } from '../../services/authentication.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css'],
  standalone: true,
  imports: [LoginFormComponent],
})
export class LoginPageComponent {

  constructor(private authService: AuthenticationService, private router: Router) {}

  onLogin(userCredentials: UserCredentials) {
    // À faire

    this.authService.login(userCredentials).subscribe(response => {
      if (response.success) {
        console.log('Login successful, redirecting to chat page...');
        this.router.navigate(['/chat']); 
      } else {
        console.error('Login failed:', response.success);
      }
    });

  }
}
