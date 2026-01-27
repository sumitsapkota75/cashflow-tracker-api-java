package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentPlan {

    private LocalDateTime date;
    private BigDecimal amount;
    private String status;
}