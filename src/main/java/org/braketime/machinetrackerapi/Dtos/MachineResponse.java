package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MachineResponse {
    private String id;
    private String businessId;
    private String name;          // Machine 1
    private String serialNumber;
    private LocalDateTime createdAt;
}
