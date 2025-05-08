package com.ironhack.TaskManager.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ironhack.TaskManager.models.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling JWT (JSON Web Token) operations.
 * This class provides methods to generate, verify, and extract information from JWT tokens.
 */
@Service
public class JwtService {

    // Secret key used for signing and verifying JWT tokens.
    private static final String SECRET = "TaskManagerSecretKey";

    /**
     * Generates a JWT token for a given user.
     * The token includes the username, roles, issue date, and expiration date.
     *
     * @param user The user for whom the token is generated.
     * @return A signed JWT token as a String.
     */
    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET); // Algorithm used for signing the token.

        return JWT.create()
                .withSubject(user.getUsername()) // Adds the username as the subject of the token.
                .withClaim("roles", user.getRoles().stream() // Adds the user's roles as a claim.
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .withIssuedAt(new Date()) // Sets the issue date of the token.
                .withExpiresAt(Date.from(Instant.now().plus(1, ChronoUnit.DAYS))) // Sets the expiration date (1 day from now).
                .sign(algorithm); // Signs the token with the specified algorithm.
    }

    /**
     * Verifies the validity of a given JWT token.
     *
     * @param token The JWT token to verify.
     * @return True if the token is valid, false otherwise.
     */
    public boolean verifyToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SECRET)) // Configures the verifier with the secret key.
                    .build()
                    .verify(token); // Verifies the token.
            return true; // Token is valid.
        } catch (Exception e) {
            return false; // Token is invalid or verification failed.
        }
    }

    /**
     * Extracts the username (subject) from a given JWT token.
     *
     * @param token The JWT token from which to extract the username.
     * @return The username as a String.
     */
    public String extractUsername(String token) {
        return JWT.decode(token).getSubject(); // Decodes the token and retrieves the subject (username).
    }

    /**
     * Extracts the roles from a given JWT token.
     *
     * @param token The JWT token from which to extract the roles.
     * @return A list of roles as Strings.
     */
    public List<String> extractRoles(String token) {
        return JWT.decode(token).getClaim("roles").asList(String.class); // Decodes the token and retrieves the roles claim.
    }
}