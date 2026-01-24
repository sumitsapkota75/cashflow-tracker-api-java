package org.braketime.machinetrackerapi.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterUserRequest {
    @Email(message="Invalid email format")
    @NotBlank(message="Email is required")
    private String email;

    @NotBlank(message="Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private  String password;

    @NotBlank(message="Role is required")
    private String role;
    @NotBlank(message="Business is required")
    private String businessId;
}
