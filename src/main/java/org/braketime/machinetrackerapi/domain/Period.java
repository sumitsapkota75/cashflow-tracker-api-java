package org.braketime.machinetrackerapi.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.braketime.machinetrackerapi.enums.PeriodStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDate businessDate; // 2026-01-15

    private PeriodStatus status;
    // OPEN, CLOSED

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private String openedByUserId;
    private String closedByUserId;
}

