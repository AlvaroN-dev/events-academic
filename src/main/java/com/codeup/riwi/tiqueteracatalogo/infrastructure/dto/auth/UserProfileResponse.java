package com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

/**
 * DTO for user profile information.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User profile information")
public class UserProfileResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "User roles", example = "[\"ROLE_USER\"]")
    private Set<String> roles;

    @Schema(description = "Account enabled status", example = "true")
    private boolean enabled;
}
