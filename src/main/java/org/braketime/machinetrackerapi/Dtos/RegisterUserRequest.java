package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class RegisterUserRequest {
    private String email;
    private  String password;
    private String role;
    private UUID businessId;
}
