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
import java.time.LocalDateTime;

@Document("winners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Winner {

    @Id
    private String id;

    @Indexed
    private String businessId;

    private String playerName;
    private String playerContact;
    private LocalDateTime winningDate;

    private BigDecimal totalWinAmount;
    private BigDecimal amountPaid;
    private BigDecimal remainingAmount;

    private String status;

    private LocalDateTime createdAt;
    private String createdByUsername;
}
