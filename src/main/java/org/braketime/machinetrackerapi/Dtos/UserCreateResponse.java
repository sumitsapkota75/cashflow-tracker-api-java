package org.braketime.machinetrackerapi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserCreateResponse {
    private String id;
    private String email;
    private String roleId;
    private String businessId;
    private boolean isActive;
}
