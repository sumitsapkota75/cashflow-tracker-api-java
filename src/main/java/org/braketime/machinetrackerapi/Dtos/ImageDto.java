package org.braketime.machinetrackerapi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private String fileId;
    private String filename;
    private String url;
    private String contentType;
    private Long size;
    private LocalDateTime uploadedAt;
}
