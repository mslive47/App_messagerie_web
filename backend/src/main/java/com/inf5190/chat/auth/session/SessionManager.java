package com.inf5190.chat.auth.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    public SessionManager() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    public String addSession(SessionData authData) {
        //final String sessionId = this.generateSessionId();
        //this.sessions.put(sessionId, authData);
        //return sessionId;

        // Date actuelle pour l'émission du jeton
        Date now = new Date();

        // Date d'expiration fixée à 2 heures après l'émission
        Date expiration = new Date(now.getTime() + TimeUnit.HOURS.toMillis(2));

        // Création du JWT
        return Jwts.builder()
                .setAudience("user") // Audience pour le jeton, ici "user" (vous pouvez adapter selon vos besoins)
                .setIssuedAt(now) // Date de création du jeton
                .setSubject(authData.username()) // Sujet du jeton, ici le nom d’utilisateur
                .setExpiration(expiration) // Expiration fixée à 2 heures après l’émission
                .signWith(this.secretKey) // Signature du jeton avec la clé secrète
                .compact(); // Génération du jeton compacté
    }

    public void removeSession(String sessionId) {
        this.sessions.remove(sessionId);
    }

    public SessionData getSession(String jwtToken) {
        //return this.sessions.get(sessionId);
        try {
            // Parse et valide le JWT pour obtenir les claims
            Jws<Claims> claimsJws = this.jwtParser.parseSignedClaims(jwtToken);
            Claims claims = claimsJws.getPayload();

            // Extraire le nom d'utilisateur (subject) du jeton
            String username = claims.getSubject();

            // Créer et retourner une nouvelle instance de SessionData avec le nom d'utilisateur
            return new SessionData(username);

        } catch (JwtException e) {
            // En cas d'échec de décodage ou de validation du JWT, retourner null
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
