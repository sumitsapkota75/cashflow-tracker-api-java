package org.braketime.machinetrackerapi.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "machine_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineEntry {

    @Id
    private UUID id;

    private UUID businessId;
    private String userId;

    private Double cashAmount;
    private Double safeDropped;
    private String reason;

    private boolean shiftClose;

    private Integer imageCount;

    private Double totalCashIn;
    private Double totalVoucherOut;
    private Double totalNet;

    private Instant createdAt;
}
