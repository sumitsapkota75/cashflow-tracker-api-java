package org.braketime.machinetrackerapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.braketime.machinetrackerapi.enums.PeriodStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Document("winner_payouts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinnerPayout {

    @Id
    private String id;

    @Indexed
    private String winnerId;
    private String winnerName;

    @Indexed
    private  String periodId;

    @Indexed
    private String businessId;

    private BigDecimal amount = BigDecimal.ZERO;

    private LocalDateTime payoutDate;

    private String status;
    // SCHEDULED, PAID

    private String remarks;

    private String createdByUser;
    private String reasonType;
}
