import { TestBed } from '@angular/core/testing';
//import { provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing'; // Réutilisation du module pour les tests HTTP
import { AuthenticationService } from './authentication.service';

describe('AuthenticationService', () => {
  let service: AuthenticationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule], // Ajoutez HttpClientTestingModule dans les imports
      providers: [AuthenticationService], // Déclarez AuthenticationService dans les providers
    });
    service = TestBed.inject(AuthenticationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy(); // Vérifie que le service a été créé correctement
  });
});
