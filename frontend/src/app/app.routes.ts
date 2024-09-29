import { Routes } from '@angular/router';
import { LoginPageComponent } from './login/pages/login-page/login-page.component';
import { ChatPageComponent } from './chat/pages/chat-page/chat-page.component';


export const routes: Routes = [
    { path: 'login', component: LoginPageComponent },
    { path: 'chat', component: ChatPageComponent },
    { path: '**', component: LoginPageComponent }
];
