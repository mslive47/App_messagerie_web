package com.inf5190.chat.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service utilisé par le AuthController
 */
@Service
public class AuthService {
    private static final String SESSION_ID_COOKIE_NAME = "sid";

    public AuthService() {
    }

    /**
     * Cette methode permet de creer un cookie
     * @param sessionId l'id de la session
     * @return le cookie
     * */
    public ResponseCookie sessionCookie(String sessionId) {
        ResponseCookie sessionCookie;
        sessionCookie = ResponseCookie.from(SESSION_ID_COOKIE_NAME, sessionId)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        return sessionCookie;
    }

    /**
     * Cette methode permet de supprimer un cookie
     * */
    public ResponseCookie deleteCookie() {
        ResponseCookie deleteCookie;
        deleteCookie = ResponseCookie.from(SESSION_ID_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return deleteCookie;
    }

}
