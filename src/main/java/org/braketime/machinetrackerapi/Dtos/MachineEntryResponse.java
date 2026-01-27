package org.braketime.machinetrackerapi.Dtos;

import lombok.Builder;
import lombok.Data;
import org.braketime.machinetrackerapi.enums.OpenReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MachineEntryResponse {

    private String entryId;

    private String periodId;
    private String machineId;

    private OpenReason reason;

    // Echoed inputs (for UI confirmation)
    private BigDecimal reportCashIn;
    private BigDecimal reportCashOut;
    private BigDecimal physicalCash;
    private BigDecimal safeDroppedAmount;

    // System-calculated
    private BigDecimal netFromReport;
    private BigDecimal difference;

    private LocalDateTime openedAt;

    private String username;
}
