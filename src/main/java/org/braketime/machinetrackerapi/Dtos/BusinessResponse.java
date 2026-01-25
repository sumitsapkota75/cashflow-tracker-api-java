package org.braketime.machinetrackerapi.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BusinessResponse {
    private String id;
    private String name;
    private String location;
    private Integer numberOfMachines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean active;
}
