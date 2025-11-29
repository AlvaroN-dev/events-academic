package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for JWT token generation, validation, and parsing.
 * 
 * <p>This service handles:</p>
 * <ul>
 *   <li>Token generation with claims (username, roles, etc.)</li>
 *   <li>Token validation and signature verification</li>
 *   <li>Token parsing and claim extraction</li>
 *   <li>Token expiration management</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.issuer:tiquetera-catalog}")
    private String jwtIssuer;

    // ========================================================================
    // Token Generation
    // ========================================================================

    /**
     * Generates a JWT token for the authenticated user.
     * 
     * @param authentication the authentication object
     * @return the generated JWT token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails);
    }

    /**
     * Generates a JWT token for the user details.
     * 
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        
        // Add roles to claims
        String roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        extraClaims.put("roles", roles);
        
        return generateToken(extraClaims, userDetails);
    }

    /**
     * Generates a JWT token with custom claims.
     * 
     * @param extraClaims additional claims to include
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuer(jwtIssuer)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    // ========================================================================
    // Token Validation
    // ========================================================================

    /**
     * Validates a JWT token.
     * 
     * @param token the JWT token
     * @param userDetails the user details to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username != null 
                && username.equals(userDetails.getUsername()) 
                && !isTokenExpired(token);
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates the token structure and signature without user context.
     * 
     * @param token the JWT token
     * @return true if the token structure is valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the token is expired.
     * 
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // ========================================================================
    // Claim Extraction
    // ========================================================================

    /**
     * Extracts the username from the token.
     * 
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     * 
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the roles from the token.
     * 
     * @param token the JWT token
     * @return the roles as a comma-separated string
     */
    public String extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }

    /**
     * Extracts a specific claim from the token.
     * 
     * @param token the JWT token
     * @param claimsResolver function to extract the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the token.
     * 
     * @param token the JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Gets the signing key from the secret.
     * 
     * @return the signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gets the token expiration time in seconds.
     * 
     * @return expiration time in seconds
     */
    public long getExpirationInSeconds() {
        return jwtExpiration / 1000;
    }

    /**
     * Gets the token expiration time in milliseconds.
     * 
     * @return expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Generates a refresh token with extended expiration.
     * Refresh tokens have 7x longer expiration than access tokens.
     * 
     * @param userDetails the user details
     * @return the generated refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Date now = new Date();
        // Refresh token expira en 7 días (7x el tiempo del access token)
        Date expiryDate = new Date(now.getTime() + (jwtExpiration * 7));

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuer(jwtIssuer)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Validates a JWT token against user details.
     * 
     * @param token the JWT token
     * @param userDetails the user details to validate against
     * @return true if the token is valid
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails);
    }
}
