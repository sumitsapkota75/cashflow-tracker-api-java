package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OpenPeriodRequest {
    private String businessId;
    private LocalDate businessDate;
    private BigDecimal totalCashInOpen;
    private BigDecimal totalCashOutOpen;
}
