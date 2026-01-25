package org.braketime.machinetrackerapi.Dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BusinessCreateUpdateRequest {
    @NotBlank(message = "Business name is required")
    private String name;
    private String location;
    @NotNull(message = "Number of machines is required")
    @Min(value = 1, message = "Number of machines must be at least 1")
    private Integer numberOfMachines;

}
