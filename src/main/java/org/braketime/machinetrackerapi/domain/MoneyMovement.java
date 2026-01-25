package org.braketime.machinetrackerapi.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.braketime.machinetrackerapi.enums.MovementTypes;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document("money_movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyMovement {

    @Id
    private String id;

    @Indexed
    private String businessId;

    @Indexed
    private String periodId;

    private String machineEntryId; // nullable

    private MovementTypes type;
    // PAYOUT, SAFE_DROP, BANK_DEPOSIT, ATM_LOAD

    private BigDecimal amount;

    private String referenceId;
    // winnerId, bankSlipId, etc.

    private String createdByUserId;

    @Indexed
    private LocalDateTime createdAt;

    private String remarks;
}
