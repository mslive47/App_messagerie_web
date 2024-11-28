import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthenticationService } from './authentication.service';
import { environment } from 'src/environments/environment';

describe('AuthenticationService', () => {
  let service: AuthenticationService;
  let httpTestingController: HttpTestingController;

  const loginData = {
    username: 'username',
    password: 'pwd',
  };

  afterEach(() => {
    localStorage.clear();
  });

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
      });
    service = TestBed.inject(AuthenticationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('on login', () => {
    beforeEach(() => {
      localStorage.clear();
      httpTestingController = TestBed.inject(HttpTestingController);
      //service = TestBed.inject(AuthenticationService);
    });

    it('should call POST with login data to auth/login', async () => {
      const loginPromise = service.login(loginData);

      const req = httpTestingController.expectOne(
        `${environment.backendUrl}/auth/login`
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginData);
      req.flush({ username: loginData.username });

      // wait for the login to complete
      await loginPromise;
    });

    it('should store and emit the username', async () => {
      // À compléter
      const loginPromise = service.login(loginData);

      const req = httpTestingController.expectOne(
        `${environment.backendUrl}/auth/login`
      );
      req.flush(
        { username: loginData.username },
        { headers: { Authorization: 'Bearer test-jwt-token' } }
      );

      await loginPromise;

      // Check localStorage values
      expect(localStorage.getItem(AuthenticationService.KEY)).toBe(loginData.username);
      expect(localStorage.getItem(AuthenticationService.TOKEN_KEY)).toBe('test-jwt-token');

      // Check service signals
      expect(service.getUsername()()).toBe(loginData.username);
      expect(service.getToken()).toBe('test-jwt-token');

    });
  });

  describe('on logout', () => {
    beforeEach(() => {
      localStorage.setItem('username', loginData.username);

      httpTestingController = TestBed.inject(HttpTestingController);
      //service = TestBed.inject(AuthenticationService);
    });

    it('should call POST with login data to auth/logout', async () => {
      // À compléter
      service['jwtToken'].set(' test-jwt-token');
      const logoutPromise = service.logout();

      const req = httpTestingController.expectOne(
        `${environment.backendUrl}/auth/logout`
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('Authorization')).toBe('Bearer test-jwt-token');
      req.flush(null);

      await logoutPromise;
    });

    it('should remove the username from the service and local storage', async () => {
      // À compléter
      const logoutPromise = service.logout();

      const req = httpTestingController.expectOne(
        `${environment.backendUrl}/auth/logout`
      );
      req.flush(null);

      await logoutPromise;

      // Check localStorage values
      expect(localStorage.getItem(AuthenticationService.KEY)).toBeNull();
      expect(localStorage.getItem(AuthenticationService.TOKEN_KEY)).toBeNull();

      // Check service signals
      expect(service.getUsername()()).toBeNull();
      expect(service.getToken()).toBeNull();
    });
  });


});
