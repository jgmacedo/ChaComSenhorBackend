package joao.ChaComOSenhor.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import joao.ChaComOSenhor.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service class for handling JWT token generation and validation.
 */
@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the user for whom the token is generated
     * @return the generated JWT token
     * @throws RuntimeException if an error occurs while generating the token
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("ChaComOSenhor")
                    .withSubject(user.getLogin())
                    .withClaim("role", user.getRole().toString())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token", e);
        }
    }

    /**
     * Validates the given JWT token and returns the subject (user login).
     *
     * @param token the JWT token to validate
     * @return the subject (user login) if the token is valid, or an empty string if invalid
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ChaComOSenhor")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return "";
        }
    }

    /**
     * Generates the expiration date for the JWT token (1 hour from now, UTC).
     *
     * @return the expiration date as an Instant
     */
    private Instant genExpirationDate() {
        // Corrected logic: 1 hour from the current UTC instant
        return Instant.now().plus(1, ChronoUnit.HOURS);
    }
}