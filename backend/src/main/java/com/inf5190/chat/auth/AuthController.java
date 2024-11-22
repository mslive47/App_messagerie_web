package com.inf5190.chat.auth;

import com.inf5190.chat.auth.session.SessionData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.session.SessionManager;
import jakarta.servlet.http.Cookie;

import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

/**
 * Contrôleur qui gère l'API de login et logout.
 */
@RestController()
public class AuthController {
    public static final String AUTH_LOGIN_PATH = "/auth/login";
    public static final String AUTH_LOGOUT_PATH = "/auth/logout";
    public static final String SESSION_ID_COOKIE_NAME = "sid";

    private final SessionManager sessionManager;
    private AuthService authService;
    private PasswordEncoder passwordEncoder;

    public AuthController(SessionManager sessionManager, AuthService authService, PasswordEncoder passwordEncoder) {
        this.sessionManager = sessionManager;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cette methode permet d'enregistrer un utilisateur
     * @param loginRequest les infos de l'utilisateur
     * @return le json web token
     * */
    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws ExecutionException, InterruptedException {
      
      try {

      
        String encodedPassword = this.passwordEncoder.encode(loginRequest.password());
        this.authService.addUser(loginRequest.username(), loginRequest.password(), encodedPassword);
        SessionData session  = new SessionData(loginRequest.username());
        String jwtToken = this.sessionManager.addSession(session);
        LoginResponse loginResponse = new LoginResponse(loginRequest.username());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer" + jwtToken)
                .body(loginResponse);

      } catch (ResponseStatusException e) {
        // Si une exception est levée, retournez un statut 403 ou un autre code d'erreur
        return ResponseEntity.status(e.getStatusCode()).body(null);
      }
    }

    /**
     * Cette methode permet de supprimer un utilisateur
     * @param authHeader infos de la session
     * */
    @PostMapping(AUTH_LOGOUT_PATH)
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(6);
            this.sessionManager.removeSession(token); // Method to invalidate token if applicable
        }

        return ResponseEntity.ok().build();
    }
}
