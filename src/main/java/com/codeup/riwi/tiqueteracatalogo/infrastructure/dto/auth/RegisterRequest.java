package com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth;

import com.codeup.riwi.tiqueteracatalogo.application.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for user registration requests.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User registration request")
public class RegisterRequest {

    @Schema(description = "Username (3-50 characters, alphanumeric and underscore)", 
            example = "john_doe", required = true)
    @NotBlank(message = "{auth.username.required}")
    @Size(min = 3, max = 50, message = "{auth.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{auth.username.pattern}")
    private String username;

    @Schema(description = "Email address", example = "john.doe@example.com", required = true)
    @NotBlank(message = "{auth.email.required}")
    @Email(message = "{auth.email.invalid}")
    @Size(max = 100, message = "{auth.email.size}")
    private String email;

    @Schema(description = "Password (min 8 characters, must contain uppercase, lowercase, digit, and special character)", 
            example = "SecureP@ss123", required = true)
    @NotBlank(message = "{auth.password.required}")
    @Size(min = 8, max = 100, message = "{auth.password.size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "{auth.password.pattern}")
    private String password;

    @Schema(description = "First name", example = "John")
    @Size(max = 100, message = "{auth.firstName.size}")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    @Size(max = 100, message = "{auth.lastName.size}")
    private String lastName;

    @Schema(description = "Phone number", example = "+1234567890")
    @Size(max = 20, message = "{auth.phone.size}")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "{auth.phone.pattern}")
    private String phone;
}
