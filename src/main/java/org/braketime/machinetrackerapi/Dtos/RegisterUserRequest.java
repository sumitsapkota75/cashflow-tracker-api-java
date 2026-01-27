package org.braketime.machinetrackerapi.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message="username is required")
    private String username;

    @NotBlank(message="Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private  String password;

    @NotBlank(message="Role is required")
    private String role;
    @NotBlank(message="Business is required")
    private String businessId;

}
