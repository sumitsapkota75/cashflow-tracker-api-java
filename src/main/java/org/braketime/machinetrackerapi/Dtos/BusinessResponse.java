package org.braketime.machinetrackerapi.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class BusinessResponse {
    private String id;
    private String name;
    private String location;
    private List<String> machineIds;
    private Integer numberOfMachines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean active;
}
