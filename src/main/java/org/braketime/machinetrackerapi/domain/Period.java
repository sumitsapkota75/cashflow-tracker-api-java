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
import java.util.List;

@Document("periods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Period {

    @Id
    private String id;

    @Indexed
    private String businessId;

    @Indexed
    private LocalDate businessDate;

    private PeriodStatus status;

    private BigDecimal totalCashInOpen = BigDecimal.ZERO;
    private BigDecimal totalCashOutOpen = BigDecimal.ZERO;
    private BigDecimal netOpen = BigDecimal.ZERO;

    private BigDecimal totalCashInClose = BigDecimal.ZERO;
    private BigDecimal totalCashOutClose = BigDecimal.ZERO;
    private BigDecimal netClose = BigDecimal.ZERO;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private BigDecimal safeDrop = BigDecimal.ZERO;
    private BigDecimal payout = BigDecimal.ZERO;
    private BigDecimal physicalCashCollected = BigDecimal.ZERO;

    private String openedByUserId;
    private String closedByUserId;

    private List<String> images;
}
