package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

}
