package com.inf5190.chat.auth;

import com.inf5190.chat.auth.session.SessionData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.session.SessionManager;
import jakarta.servlet.http.Cookie;

/**
 * Contrôleur qui gère l'API de login et logout.
 */
@RestController()
public class AuthController {
    public static final String AUTH_LOGIN_PATH = "/auth/login";
    public static final String AUTH_LOGOUT_PATH = "/auth/logout";
    public static final String SESSION_ID_COOKIE_NAME = "sid";

    private final SessionManager sessionManager;
    private final AuthService authService;

    public AuthController(SessionManager sessionManager, AuthService authService) {
        this.sessionManager = sessionManager;
        this.authService = authService;
    }

    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // À faire...
        SessionData session  = new SessionData(loginRequest.username());
        String sessionId =  this.sessionManager.addSession(session);
        LoginResponse loginResponse = new LoginResponse(loginRequest.username());
        ResponseCookie responseCookie = this.authService.sessionCookie(sessionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponse);
    }

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
