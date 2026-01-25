package org.braketime.machinetrackerapi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BusinessSummaryResponse{
    private String id;
    private String name;
    private String location;
    private LocalDateTime createdAt;
    private Integer numberOfMachines;
}