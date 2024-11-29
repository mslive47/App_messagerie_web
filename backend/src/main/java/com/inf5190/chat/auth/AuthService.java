package com.inf5190.chat.auth;

import com.inf5190.chat.auth.repository.FirestoreUserAccount;
import com.inf5190.chat.auth.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

/**
 * Service utilisé par le AuthController
 */
@Service
public class AuthService {
    private static final String SESSION_ID_COOKIE_NAME = "sid";
    private UserAccountRepository userAccountRepository;
    private PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
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
                .sameSite("None")
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
                .sameSite("None")
                .build();
        return deleteCookie;
    }

    /**
     * Cette methode permet d'ajouter un utilisateur à la db
     * @param name le nom de l'utilisateur
     * @param password le mot de passe
     * @param encodedPassword le mot de passe chiffré
     * */
    public void addUser(String name, String password, String encodedPassword) throws ExecutionException, InterruptedException {
        FirestoreUserAccount userOnFirestore = this.userAccountRepository.getUserAccount(name);
        if (userOnFirestore == null) {

             System.out.println("User not found, creating new user.");
            FirestoreUserAccount firestoreUserAccount = new FirestoreUserAccount(name, encodedPassword);
            this.userAccountRepository.createUserAccount(firestoreUserAccount);
        } else {

             System.out.println("User found, checking password.");
            boolean samePassword = this.passwordEncoder.matches(password, userOnFirestore.getEncodedPassword());
            if(!samePassword) {
                System.out.println("Passwords do not match");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

    }

}
