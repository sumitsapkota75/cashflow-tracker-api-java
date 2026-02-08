package org.braketime.machinetrackerapi.services;

import com.mongodb.client.gridfs.GridFSBucket;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.braketime.machinetrackerapi.Dtos.ImageDto;
import org.braketime.machinetrackerapi.domain.Period;
import org.braketime.machinetrackerapi.exception.BadRequestException;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.repository.PeriodRepositoy;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final PeriodRepositoy periodRepositoy;
    private final GridFSBucket gridFSBucket;

    public List<ImageDto> uploadPeriodImages(String periodId, List<MultipartFile> files) throws RuntimeException {

        Period period = periodRepositoy.findById(periodId).orElseThrow(() -> new NotFoundException("Period not found"));

        if (period.getImages() == null) period.setImages(new ArrayList<>());
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("No files provided");
        }

        List<ImageDto> imageRefs = new ArrayList<>();
        List<ObjectId> uploadedFileIds = new ArrayList<>(); // for rollback

        try {
            for (MultipartFile file :files){
                if (file.isEmpty()) continue;
                String ct = file.getContentType();
                if (ct == null || !ct.startsWith("image/")) {
                    throw new BadRequestException("Only image files are allowed");
                }
                ObjectId fileId;
                try(InputStream in = file.getInputStream()){
                    // store image in grid fs
                    fileId = gridFSBucket.uploadFromStream(
                            Objects.requireNonNull(file.getOriginalFilename()),
                            in,
                            new com.mongodb.client.gridfs.model.GridFSUploadOptions().metadata(new Document())
                    );
                }catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
                uploadedFileIds.add(fileId);
                ImageDto ref = ImageDto.builder()
                        .fileId(fileId.toHexString())
                        .filename(file.getOriginalFilename())
                        .url("/files/" + fileId.toHexString())
                        .contentType(ct)
                        .size(file.getSize())
                        .uploadedAt(LocalDateTime.now())
                        .build();
                period.getImages().add(ref);
                imageRefs.add(ref);
                periodRepositoy.save(period);
                return imageRefs;
            }
        } catch (Exception ex) {
//            throw new RuntimeException("Failed to upload image: "+ file.getO);
            // ✅ rollback: delete uploaded GridFS files if period save fails
            for (ObjectId fileId : uploadedFileIds) {
                try {
                    gridFSBucket.delete(fileId);
                } catch (Exception ignored) {
                    // If delete fails, you may log it; don't hide original exception
                }
            }

        }
        return imageRefs;
    }
}
