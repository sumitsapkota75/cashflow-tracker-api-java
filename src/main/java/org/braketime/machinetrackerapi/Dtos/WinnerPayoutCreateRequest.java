package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WinnerPayoutCreateRequest {
    private String periodId;
    private String winnerID;
    private String winnerName;
    private BigDecimal amount;
    private LocalDate payoutDate;
    private String status;
    private String remarks;
    private String reasonType;
}
