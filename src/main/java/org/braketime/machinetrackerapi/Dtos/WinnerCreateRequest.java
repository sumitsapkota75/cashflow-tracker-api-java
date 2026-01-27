package org.braketime.machinetrackerapi.Dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WinnerCreateRequest {
    private String playerName;
    private String playerContact;
    private LocalDateTime winningDate;

    private BigDecimal totalWinAmount;
    private BigDecimal amountPaid;
    private BigDecimal remainingAmount;
    private String status;
}
