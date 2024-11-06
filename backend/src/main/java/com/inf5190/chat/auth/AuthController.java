package com.inf5190.chat.auth;

import com.inf5190.chat.auth.session.SessionData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.session.SessionManager;
import jakarta.servlet.http.Cookie;

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
     * @return le cookie
     * */
    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws ExecutionException, InterruptedException {
        // À faire...
        String encodedPassword = this.passwordEncoder.encode(loginRequest.password());
        this.authService.addUser(loginRequest.username(), loginRequest.password(), encodedPassword);
        SessionData session  = new SessionData(loginRequest.username());
        //String sessionId =  this.sessionManager.addSession(session);
        // Créer un jeton JWT pour cette session
        String jwtToken = this.sessionManager.addSession(session);
        LoginResponse loginResponse = new LoginResponse(loginRequest.username());
        //ResponseCookie responseCookie = this.authService.sessionCookie(sessionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer" + jwtToken)
                .body(loginResponse);
    }

    /**
     * Cette methode permet de supprimer un utilisateur
     * @param sessionCookie les infos de la session
     * */
    @PostMapping(AUTH_LOGOUT_PATH)
    public ResponseEntity<Void> logout(@CookieValue(SESSION_ID_COOKIE_NAME) Cookie sessionCookie) {
        // À faire...
        String sessionId = sessionCookie.getValue();
        if (sessionId != null) {
            this.sessionManager.removeSession(sessionId);
        }
        ResponseCookie deleteCookie = this.authService.deleteCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
