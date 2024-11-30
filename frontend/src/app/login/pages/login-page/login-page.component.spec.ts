import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginPageComponent } from './login-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { LoginFormComponent } from '../../components/login-form/login-form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthenticationService } from '../../services/authentication.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'; // Import de BrowserAnimationsModule

describe('LoginPageComponent', () => {
  let component: LoginPageComponent;
  let fixture: ComponentFixture<LoginPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        LoginPageComponent,
        LoginFormComponent,
        HttpClientTestingModule,
        BrowserAnimationsModule, // Ajout de BrowserAnimationsModule
      ],
      providers: [AuthenticationService],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
