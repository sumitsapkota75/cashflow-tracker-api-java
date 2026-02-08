package org.braketime.machinetrackerapi.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.braketime.machinetrackerapi.services.ImageStorageService;
import org.bson.Document;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final ImageStorageService imageStorageService;

    @GetMapping("/files/{id}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable String id) {
        GridFSFile file = imageStorageService.findById(id);

        String filename = file.getFilename() == null ? "file" : file.getFilename();
        String safeFilename = toAsciiFilename(filename);
        String contentType = null;
        Document metadata = file.getMetadata();
        if (metadata != null && metadata.getString("contentType") != null) {
            contentType = metadata.getString("contentType");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline().filename(safeFilename).build());
        if (contentType != null) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        headers.setContentLength(file.getLength());

        StreamingResponseBody body = outputStream -> imageStorageService.downloadToStream(id, outputStream);
        return ResponseEntity.ok().headers(headers).body(body);
    }

    private static String toAsciiFilename(String filename) {
        if (filename == null || filename.isBlank()) return "file";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filename.length(); i++) {
            char c = filename.charAt(i);
            if (c >= 32 && c <= 126) {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        String safe = sb.toString().trim();
        return safe.isBlank() ? "file" : safe;
    }
}
