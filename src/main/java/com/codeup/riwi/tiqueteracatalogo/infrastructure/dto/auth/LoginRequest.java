package com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for login requests.
 * Supports login with either username or email.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Login request with username/email and password")
public class LoginRequest {

    @Schema(description = "Username or email address", example = "john_doe", required = true)
    @NotBlank(message = "{auth.usernameOrEmail.required}")
    private String usernameOrEmail;

    @Schema(description = "Password", example = "SecureP@ss123", required = true)
    @NotBlank(message = "{auth.password.required}")
    private String password;
}
