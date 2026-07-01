package com.expensetracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Generates and validates JSON Web Tokens using the JJWT 0.12.x API.
 *
 * <p>The secret is read from {@code app.jwt.secret} as a Base64-encoded string
 * so that it can be safely stored in environment variables or a secrets manager
 * without binary encoding issues.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Derives the HMAC-SHA signing key from the Base64-encoded secret property.
     * Called lazily so the key is re-derived from the property each time,
     * allowing the secret to be rotated without restarting the JVM (via
     * Spring Cloud Config refresh, for example).
     */
    private SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Creates a signed JWT for an authenticated user.
     *
     * <ul>
     *   <li>Subject  → username</li>
     *   <li>Claim    → {@code userId} (Long)</li>
     *   <li>Expiry   → {@code now + app.jwt.expiration-ms}</li>
     * </ul>
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
            .subject(principal.getUsername())
            .claim("userId", principal.getId())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(signingKey())
            .compact();
    }

    /** Extracts the username (JWT subject) from a valid, signed token. */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    /**
     * Validates the token's signature and expiration.
     * Returns {@code false} and logs the reason instead of throwing, so the
     * filter chain can continue and let Spring Security return a 401.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
