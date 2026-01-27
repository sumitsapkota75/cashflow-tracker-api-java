package org.braketime.machinetrackerapi.Dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class WinnerCreateRequest {
    private String playerName;
    private String playerContact;
    private LocalDateTime winningDate;

    private BigDecimal totalWinAmount;
    private BigDecimal amountPaid;
    private String status;
    private List<PaymentPlan> paymentPlan;
}
