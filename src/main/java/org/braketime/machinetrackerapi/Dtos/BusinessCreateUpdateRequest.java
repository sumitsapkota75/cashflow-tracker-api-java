package org.braketime.machinetrackerapi.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BusinessCreateUpdateRequest {
    @NotBlank(message = "Business name is required")
    private String name;

    private String location;

}
