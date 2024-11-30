import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.repository.FirestoreUserAccount;
import com.inf5190.chat.auth.repository.UserAccountRepository;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;

import com.inf5190.chat.auth.AuthController;
import com.inf5190.chat.auth.AuthService;
import static org.mockito.Mockito.doNothing;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public class TestAuthController {

        private final String username = "username";
        private final String password = "pwd";
        private final String hashedPassword = "hash";
        private final FirestoreUserAccount userAccount = new FirestoreUserAccount(this.username, this.hashedPassword);

        private final LoginRequest loginRequest = new LoginRequest(this.username, this.password);

        @Mock
        private SessionManager mockSessionManager;

        @Mock
        private UserAccountRepository mockAccountRepository;

        @Mock
        private PasswordEncoder mockPasswordEncoder;

        @Mock
        private AuthService mockAuthService;

        @Mock
        private AuthController authController;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                this.authController = new AuthController(mockSessionManager, mockAuthService, mockPasswordEncoder);
        }

        /**
         * Test du login avec un utilisateur existant et le bon mot de passe.
         */
        @Test
        public void loginExistingUserAccountWithCorrectPassword()
                        throws InterruptedException, ExecutionException {
                final SessionData expectedSessionData = new SessionData(this.username);
                final String expectedUsername = this.username;

                // Mock de l'encodage du mot de passe
                when(this.mockPasswordEncoder.encode(loginRequest.password())).thenReturn(this.hashedPassword);

                // Simuler le comportement de la méthode addUser dans AuthService
                doNothing().when(this.mockAuthService).addUser(loginRequest.username(), loginRequest.password(),
                                this.hashedPassword);

                // Mock de la récupération de l'utilisateur et de la validation du mot de passe
                when(this.mockAccountRepository.getUserAccount(loginRequest.username()))
                                .thenReturn(userAccount);
                when(this.mockPasswordEncoder.matches(loginRequest.password(), this.hashedPassword))
                                .thenReturn(true);
                when(this.mockSessionManager.addSession(expectedSessionData)).thenReturn(expectedUsername);

                // Appel de la méthode login
                ResponseEntity<LoginResponse> response = this.authController.login(loginRequest);

                // Vérification de la réponse
                assertThat(response.getStatusCode().value()).isEqualTo(200);
                assertThat(response.getBody().username()).isEqualTo(expectedUsername);

                // verify(this.mockPasswordEncoder, times(1)).matches(this.password,
                // this.hashedPassword);
                // Vérifier que la méthode addUser a été appelée une seule fois
                verify(this.mockAuthService, times(1)).addUser(loginRequest.username(), loginRequest.password(),
                                this.hashedPassword);

                // verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);

                verify(this.mockSessionManager, times(1)).addSession(expectedSessionData);
        }

        /**
         * Test du login avec un nouvel utilisateur (compte inexistant).
         */
        @Test
        public void loginNewUserAccount()
                        throws InterruptedException, ExecutionException {
                // Mock de l'absence d'utilisateur
                when(this.mockAccountRepository.getUserAccount(loginRequest.username()))
                                .thenReturn(null);

                when(this.mockPasswordEncoder.encode(loginRequest.password()))
                                .thenReturn(hashedPassword);

                // Simuler le comportement de la méthode addUser dans AuthService
                doNothing().when(this.mockAuthService).addUser(loginRequest.username(), loginRequest.password(),
                                this.hashedPassword);

                // Appel de la méthode login
                ResponseEntity<LoginResponse> response = this.authController.login(loginRequest);

                // Vérification de la réponse
                assertThat(response.getStatusCode().value()).isEqualTo(200);
                assertThat(response.getBody().username()).isEqualTo(loginRequest.username());

                // Vérification des appels aux mocks
                // verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);
                verify(this.mockAuthService, times(1)).addUser(loginRequest.username(), loginRequest.password(),
                                this.hashedPassword);
                verify(this.mockPasswordEncoder, times(1)).encode(this.password);
        }

        /**
         * Test du login avec un utilisateur existant et un mot de passe incorrect.
         */
        @Test
        public void loginExistingUserAccountWithIncorrectPassword()
                        throws InterruptedException, ExecutionException {

                // Mock de l'encodage du mot de passe
                when(this.mockPasswordEncoder.encode(loginRequest.password())).thenReturn(this.hashedPassword);

                // Mock de l'utilisateur existant
                when(this.mockAccountRepository.getUserAccount(loginRequest.username()))
                                .thenReturn(userAccount);

                // Mock du mot de passe incorrect
                when(this.mockPasswordEncoder.matches(loginRequest.password(), this.hashedPassword))
                                .thenReturn(false);

                ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                                () -> this.authController.login(loginRequest));
                assertThat(exception.getStatusCode()).isEqualTo(403);

                // Vérification des appels aux mocks
                verify(this.mockAuthService, times(1)).addUser(loginRequest.username(), loginRequest.password(),
                                this.hashedPassword);
                // verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);
                verify(this.mockPasswordEncoder, times(1)).matches(this.password, this.hashedPassword);
        }

        /**
         * Test d'une exception inattendue lors de l'accès à Firestore.
         */
        @Test
        public void loginWithFirestoreException()
                        throws InterruptedException, ExecutionException {
                // Simuler une exception lors de l'accès au repository
                when(this.mockAccountRepository.getUserAccount(loginRequest.username()))
                                .thenThrow(new RuntimeException("Firestore connection error"));

                // Appel de la méthode login et vérification de l'exception
                assertThrows(ResponseStatusException.class, () -> {
                        this.authController.login(loginRequest);
                });

                ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                                () -> this.authController.login(loginRequest));
                assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

                // Vérification des appels aux mocks
                verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);
        }
}