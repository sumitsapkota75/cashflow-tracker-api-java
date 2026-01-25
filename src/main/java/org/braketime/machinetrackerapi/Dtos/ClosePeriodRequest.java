package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;
import org.braketime.machinetrackerapi.enums.PeriodStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClosePeriodRequest {
    private String periodId;
    private BigDecimal totalCashInClose;
    private BigDecimal totalCashOutClose;
    private LocalDate businessDate;
}
