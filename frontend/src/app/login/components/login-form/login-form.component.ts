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
    username: ['', Validators.required], //Ajouter validators  
    password: ['', Validators.required]
  });

checkName : boolean = false;
checkPassWord : boolean = false;

  login = output<UserCredentials>();

  constructor(private fb: FormBuilder) {}

  onLogin() {
    // À faire
    const credentials: UserCredentials = {
      username: this.loginForm.value.username!,
      password: this.loginForm.value.password!,
    };
    this.loginForm.reset();
    this.login.emit(credentials);
  }

  verifyName() {
    if (this.loginForm.value.username == '') {
      this.checkName = true;
    }  
  }

  resetName() {
    this.checkName = false;
  }

  verifyPassWord() {
    if(this.loginForm.value.password == '') {
      this.checkPassWord = true;
    }
  }

  resetPass() {
    this.checkPassWord = false;
  }
}
