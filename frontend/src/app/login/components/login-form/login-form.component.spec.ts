import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { LoginFormComponent } from './login-form.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TestHelper } from 'src/app/test-helper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { By } from '@angular/platform-browser';

describe('LoginFormComponent', () => {
  let component: LoginFormComponent;
  let fixture: ComponentFixture<LoginFormComponent>;
  let testHelper: TestHelper<LoginFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
      imports: [ReactiveFormsModule, LoginFormComponent, NoopAnimationsModule,  MatFormFieldModule,
        MatInputModule,],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginFormComponent);
    component = fixture.componentInstance;
    testHelper = new TestHelper(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit username and password when form is valid', () => {
    let emittedCredentials: { username: string; password: string } | undefined;

    component.login.subscribe((event) => {
      emittedCredentials = event;
    });

    const usernameInput = testHelper.getInput('username-input');
    const passwordInput = testHelper.getInput('password-input');
    const submitButton = testHelper.getButton('submit-button');

    testHelper.writeInInput(usernameInput, 'James');
    testHelper.writeInInput(passwordInput, 'abc');
    fixture.detectChanges();

    submitButton.click();

    expect(emittedCredentials).toEqual({
      username: 'James',
      password: 'abc',
    });
    expect(component.loginForm.valid).toBe(true);
  });

  it('should show error if username is missing', () => {
    const usernameInput = fixture.debugElement.query(By.css('[data-testid="username-input"]')).nativeElement;
    const passwordInput = testHelper.getInput('password-input');
    const submitButton = testHelper.getButton('submit-button');

    usernameInput.value = ''; 
    usernameInput.dispatchEvent(new Event('input')); 
    usernameInput.dispatchEvent(new Event('blur'));  
    testHelper.writeInInput(passwordInput, 'abc');
    fixture.detectChanges();

    submitButton.click();

    const usernameError = testHelper.getElement('username-error');
    expect(usernameError).toBeTruthy();
    expect(usernameError.textContent).toContain('nom requis');
    expect(component.loginForm.valid).toBe(false);
  });

  it('should show error if password is missing', () => {
    const passwordInput = fixture.debugElement.query(By.css('[data-testid="password-input"]')).nativeElement;
    const usernameInput = testHelper.getInput('username-input');
    const submitButton = testHelper.getButton('submit-button');

    passwordInput.value = ''; 
    passwordInput.dispatchEvent(new Event('input')); 
    passwordInput.dispatchEvent(new Event('blur'));  
    testHelper.writeInInput(usernameInput, 'james');
    fixture.detectChanges();

    submitButton.click();

    const passwordError = testHelper.getElement('password-error');
    expect(passwordError).toBeTruthy();
    expect(passwordError.textContent).toContain('mot de passe requis');
    expect(component.loginForm.valid).toBe(false);
  });

  it('should show errors if both username and password are missing', () => {
    const usernameInput = fixture.debugElement.query(By.css('[data-testid="username-input"]')).nativeElement;
    const passwordInput = fixture.debugElement.query(By.css('[data-testid="password-input"]')).nativeElement;
    const submitButton = testHelper.getButton('submit-button');

    usernameInput.value = '';
    usernameInput.dispatchEvent(new Event('input'));
    usernameInput.dispatchEvent(new Event('blur'));
    

    passwordInput.value = '';
    passwordInput.dispatchEvent(new Event('input'));
    passwordInput.dispatchEvent(new Event('blur'));
    fixture.detectChanges();

    submitButton.click();

    const usernameError = testHelper.getElement('username-error');
    const passwordError = testHelper.getElement('password-error');

    expect(usernameError).toBeTruthy();
    expect(usernameError.textContent).toContain('nom requis');

    expect(passwordError).toBeTruthy();
    expect(passwordError.textContent).toContain('mot de passe requis');
    expect(component.loginForm.valid).toBe(false);
  });

});
