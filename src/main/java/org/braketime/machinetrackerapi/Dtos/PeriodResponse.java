package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;
import org.braketime.machinetrackerapi.enums.PeriodStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PeriodResponse {
    private String id;
    private String businessId;
    private LocalDate businessDate;
    private PeriodStatus status;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private BigDecimal totalCashInOpen;
    private BigDecimal totalCashOutOpen;
    private BigDecimal totalCashInClose;
    private BigDecimal totalCashOutClose;

    private String openedByUserId;
    private String closedByUserId;
}
