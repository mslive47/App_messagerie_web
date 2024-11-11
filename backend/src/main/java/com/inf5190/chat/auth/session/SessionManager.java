package com.inf5190.chat.auth.session;

import java.util.*;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Repository;

import javax.crypto.SecretKey;

/**
 * Classe qui gère les sessions utilisateur.
 * Pour le moment, on gère en mémoire.
 */
@Repository
public class SessionManager {

    private static final String SECRET_KEY_BASE64 = "EbeDt9jq+LFsx8tFTTit/gV/LrDxr3gSnHTmUXoBLzc=";
    private final SecretKey secretKey;
    private final JwtParser jwtParser;
    private final Map<String, SessionData> sessions = new HashMap<String, SessionData>();
    private final Set<String> revokedTokens = new HashSet<>();

    public SessionManager() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    /**
     * Cette methode permet d'ojouter une sessiom
     * @param authData les donnees de la session
     * @return le json web token
     * */
    public String addSession(SessionData authData) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + TimeUnit.HOURS.toMillis(2));

        // Création du JWT
        return Jwts.builder()
                .setAudience("user")
                .setIssuedAt(now)
                .setSubject(authData.username())
                .setExpiration(expiration)
                .signWith(this.secretKey)
                .compact();
    }

    /**
     * Cette methode permet de supprimer la session
     * @param token, le json web token
     * */
    public void removeSession(String token) {
        this.revokedTokens.add(token);;
    }

    /**
     * Cette methode permet d'obtenir une session
     * @param jwtToken, le token
     * @return la session
     * */
    public SessionData getSession(String jwtToken) {
        if (revokedTokens.contains(jwtToken)) {
            return null;
        }

        try {
            Jws<Claims> claimsJws = this.jwtParser.parseSignedClaims(jwtToken);
            Claims claims = claimsJws.getPayload();
            String username = claims.getSubject();
            return new SessionData(username);

        } catch (JwtException e) {
            return null;
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public String generateSecretKey() {
        SecretKey key = Jwts.SIG.HS256.key().build();
        return Encoders.BASE64.encode(key.getEncoded());
    }
}
