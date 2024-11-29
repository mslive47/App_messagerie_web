import { Routes } from '@angular/router';
import { LoginPageComponent } from './login/pages/login-page/login-page.component';
import { ChatPageComponent } from './chat/pages/chat-page/chat-page.component';
import { loginGuard } from './login/guards/login.guard';
import { loginPageGuard } from './login/guards/login-page.guard';


export const routes: Routes = [
    { path: 'login', component: LoginPageComponent, canActivate: [loginPageGuard] }, //modifier en login PageGuard
    { path: 'chat', component: ChatPageComponent, canActivate: [loginGuard] }, //canActivate: [loginGuard] 
    { path: '**', component: LoginPageComponent,  canActivate: [loginPageGuard] } //canActivate: [loginPageGuard]
];
