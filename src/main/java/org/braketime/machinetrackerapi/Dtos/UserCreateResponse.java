package org.braketime.machinetrackerapi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserCreateResponse {
    private String id;
    private String email;
    private String role;
    private String businessId;
    private boolean active;
    private BusinessSummaryResponse business;
}
