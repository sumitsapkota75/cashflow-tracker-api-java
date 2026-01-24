package org.braketime.machinetrackerapi.domain;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "businesses")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    private String id;
    private String name;
    private String location;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private boolean active = true;
}

