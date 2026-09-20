package br.com.healthtech.medplatform.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // key-word defined in application.yml
    @Value("${api.security.token.secret:my-secret-key-default-123456}")
    private String secret;

    private static final String ISSUER = "medplatform-api";

    /// Creates the JWT token (generated during login).
    public String generateToken(String email) {
        try {
            // The algorithm that will be used to encrypt the token.
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Builds the token.
            return JWT.create()
                    .withIssuer(ISSUER)         // Who created and marked the token (the API)
                    .withSubject(email)         // Token owner's identifier (the email)
                    .withExpiresAt(getExpirationDate()) // Token validation time (2 hours)
                    .sign(algorithm);           // Encrypts

        } catch (Exception exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    /// Validates the tokens (will be sent in non-public endpoints)
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Verifies and validates the token (If matches the key-word and the algorithm) and returns it.
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)              // If it's expired, throws exception here
                    .getSubject();              // If it's all alright, extracts the email (the identifier field; the 'username')

        } catch (JWTVerificationException exception) {
            // On such exception, returns null.
            return null;
        }
    }

    /// Token expiration time - 2 Hours
    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}

