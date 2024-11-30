package com.inf5190.chat.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.repository.FirestoreUserAccount;
import com.inf5190.chat.auth.repository.UserAccountRepository;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;

import org.springframework.http.HttpStatus;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.springframework.web.server.ResponseStatusException;

public class TestAuthController {

    private final String username = "username";
    private final String password = "pwd";
    private final String hashedPassword = "hash";

    private final FirestoreUserAccount userAccount =
            new FirestoreUserAccount(username, hashedPassword);
    private final LoginRequest loginRequest = new LoginRequest(username, password);

    @Mock
    private SessionManager mockSessionManager;

    @Mock
    private UserAccountRepository mockAccountRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        AuthService authService = new AuthService(mockAccountRepository, mockPasswordEncoder);
        this.authController = new AuthController(mockSessionManager, authService, mockPasswordEncoder);
    }

    /**
     * Test du login avec un utilisateur existant et le bon mot de passe.
     */
    @Test
    public void loginExistingUserAccountWithCorrectPassword()
            throws InterruptedException, ExecutionException {
        final SessionData expectedSessionData = new SessionData(username);
        final String jwtToken = "jwtToken";

        when(mockAccountRepository.getUserAccount(username)).thenReturn(userAccount);
        when(mockPasswordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(mockSessionManager.addSession(expectedSessionData)).thenReturn(jwtToken);

        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().username()).isEqualTo(username);
        verify(mockSessionManager, times(1)).addSession(expectedSessionData);
    }

    /**
     * Test du login avec un nouvel utilisateur.
     */
    @Test
    public void loginNewUserAccount() throws InterruptedException, ExecutionException {
        when(mockAccountRepository.getUserAccount(username)).thenReturn(null);
        when(mockPasswordEncoder.encode(password)).thenReturn(hashedPassword);

        SessionData expectedSessionData = new SessionData(username);
        final String jwtToken = "jwtToken";
        when(mockSessionManager.addSession(expectedSessionData)).thenReturn(jwtToken);

        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().username()).isEqualTo(username);
        verify(mockAccountRepository, times(1)).getUserAccount(username);
    }

    /**
     * Test du login avec un utilisateur existant et un mot de passe incorrect.
     */
    @Test
    public void loginExistingUserAccountWithIncorrectPassword()
            throws InterruptedException, ExecutionException {
        when(mockAccountRepository.getUserAccount(username)).thenReturn(userAccount);
        when(mockPasswordEncoder.matches(password, hashedPassword)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authController.login(loginRequest));
        verify(mockPasswordEncoder, times(1)).matches(password, hashedPassword);
    }

    /**
     * Test de propagation d'une exception.
     */
    @Test
    public void testExceptionPropagation() throws ExecutionException, InterruptedException {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Erreur simulée"))
                .when(mockAccountRepository)
                .getUserAccount(anyString());

        assertThrows(ResponseStatusException.class, () -> mockAccountRepository.getUserAccount(username));
    }


    /**
     * Test d'une exception inattendue lors de l'accès à Firestore.
     */
    @Test
    public void loginWithFirestoreException() throws ExecutionException, InterruptedException {
        when(mockAccountRepository.getUserAccount(username))
                .thenThrow(new ExecutionException("Erreur Firestore simulée", new RuntimeException()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.login(loginRequest);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(exception.getReason()).isEqualTo("Erreur inattendue lors de la connexion.");
        verify(mockAccountRepository, times(1)).getUserAccount(username);
    }

}