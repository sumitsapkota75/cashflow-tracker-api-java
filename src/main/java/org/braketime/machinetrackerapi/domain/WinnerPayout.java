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

    @Indexed
    private String periodId;

    private BigDecimal amount;

    private LocalDate payoutDate;

    private String status;
    // SCHEDULED, PAID

    private String remarks;
}
