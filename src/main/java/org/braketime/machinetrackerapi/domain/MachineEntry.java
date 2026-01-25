package org.braketime.machinetrackerapi.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.braketime.machinetrackerapi.enums.OpenReason;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document("machine_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineEntry {

    @Id
    private String id;

    @Indexed
    private String businessId;

    @Indexed
    private String periodId;

    @Indexed
    private String machineId;

    private String openedByUserId;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private OpenReason reason;
    // DAY_START, MID_DAY, PAYOUT, BANK_DEPOSIT

    // From machine slip
    private BigDecimal reportCashIn;
    private BigDecimal reportCashOut;
    private BigDecimal vouchersOut;
    private BigDecimal gamesPlayed;
    private BigDecimal amountBet;

    // Physical count
    private BigDecimal physicalCash;

    // Derived
    private BigDecimal netFromReport;
    private BigDecimal difference;


    private String remarks;
}
