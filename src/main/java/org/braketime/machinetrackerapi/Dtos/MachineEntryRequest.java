package org.braketime.machinetrackerapi.Dtos;

import lombok.Data;
import org.braketime.machinetrackerapi.enums.OpenReason;

import java.math.BigDecimal;

@Data
public class MachineEntryRequest {
    //get the latest open period and attach to that
    private String machineId;
    private OpenReason reason;

    private BigDecimal reportCashIn;
    private BigDecimal reportCashOut;

    private BigDecimal physicalCash;
    private BigDecimal netFromReport;

    private String remarks;
    private BigDecimal safeDroppedAmount;
}
