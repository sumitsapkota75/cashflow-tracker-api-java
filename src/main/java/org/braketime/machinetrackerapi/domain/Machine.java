package org.braketime.machinetrackerapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("machines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

    @Id
    private String id;

    @Indexed
    private String businessId;

    private String name;          // Machine 1
    private String serialNumber;  // From manufacturer

    private boolean active = true;

    private LocalDateTime createdAt;
}
