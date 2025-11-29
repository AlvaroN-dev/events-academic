package com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.Set;

/**
 * DTO for authentication responses containing JWT token and user info.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {

    @Schema(description = "JWT access token", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration time in seconds", example = "3600")
    private long expiresIn;

    @Schema(description = "Token expiration timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant expiresAt;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User roles", example = "[\"ROLE_USER\"]")
    private Set<String> roles;

    /**
     * Static factory method for creating a successful auth response.
     */
    public static AuthResponse success(String accessToken, long expiresInSeconds, 
                                        Long userId, String username, String email, 
                                        Set<String> roles) {
        return AuthResponse.builder()
            .accessToken(accessToken)
            .tokenType("Bearer")
            .expiresIn(expiresInSeconds)
            .expiresAt(Instant.now().plusSeconds(expiresInSeconds))
            .userId(userId)
            .username(username)
            .email(email)
            .roles(roles)
            .build();
    }
}
