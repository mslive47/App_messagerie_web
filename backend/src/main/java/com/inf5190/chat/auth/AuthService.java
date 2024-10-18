package com.inf5190.chat.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String SESSION_ID_COOKIE_NAME = "sid";

    public AuthService() {
    }

    public ResponseCookie sessionCookie(String sessionId) {
        ResponseCookie sessionCookie;
        sessionCookie = ResponseCookie.from(SESSION_ID_COOKIE_NAME, sessionId)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("None")
                .build();
        return sessionCookie;
    }

    public ResponseCookie deleteCookie() {
        ResponseCookie deleteCookie;
        deleteCookie = ResponseCookie.from(SESSION_ID_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        return deleteCookie;
    }

}
