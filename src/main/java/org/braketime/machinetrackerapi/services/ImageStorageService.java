package org.braketime.machinetrackerapi.services;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.braketime.machinetrackerapi.Dtos.ImageUploadResult;
import org.braketime.machinetrackerapi.exception.BadRequestException;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    private final GridFSBucket gridFSBucket;

    public List<ImageUploadResult> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("No files provided");
        }

        List<ImageUploadResult> results = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BadRequestException("Only image files are allowed");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                originalFilename = "upload-" + UUID.randomUUID();
            }

            ObjectId fileId;
            try (InputStream in = file.getInputStream()) {
                Document metadata = new Document();
                metadata.put("contentType", contentType);
                fileId = gridFSBucket.uploadFromStream(
                        originalFilename,
                        in,
                        new com.mongodb.client.gridfs.model.GridFSUploadOptions().metadata(metadata)
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }

            results.add(ImageUploadResult.builder()
                    .fileId(fileId.toHexString())
                    .url("/files/" + fileId.toHexString())
                    .build());
        }

        if (results.isEmpty()) {
            throw new BadRequestException("No non-empty files provided");
        }

        return results;
    }

    public void deleteImagesById(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) return;
        for (String id : fileIds) {
            if (id == null || id.isBlank()) continue;
            try {
                gridFSBucket.delete(new ObjectId(id));
            } catch (Exception ignored) {
                // best-effort cleanup
            }
        }
    }

    public GridFSFile findById(String fileId) {
        GridFSFindIterable found = gridFSBucket.find(new Document("_id", new ObjectId(fileId)));
        GridFSFile file = found.first();
        if (file == null) {
            throw new NotFoundException("File not found");
        }
        return file;
    }

    public void downloadToStream(String fileId, OutputStream outputStream) {
        gridFSBucket.downloadToStream(new ObjectId(fileId), outputStream);
    }
}
