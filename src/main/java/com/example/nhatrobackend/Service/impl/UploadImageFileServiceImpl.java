package com.example.nhatrobackend.Service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.nhatrobackend.Service.UploadImageFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadImageFileServiceImpl implements UploadImageFileService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            log.warn("UploadImage called with null or empty file");
            return null;
        }

        log.info("Attempting to upload file: {}", file.getOriginalFilename());
        String originalFilename = file.getOriginalFilename();
        String publicValue = generatePublicValue(originalFilename);
        String extension = getFileExtension(originalFilename);

        String fileUrl = null;

        try {
            byte[] fileBytes = file.getBytes(); // Get file content as byte array
            log.info("Read {} bytes from file: {}", fileBytes.length, originalFilename);

            // Determine resource type based on content type or extension
            String resourceType = "image"; // Default to image
            if (file.getContentType() != null && file.getContentType().startsWith("video")) {
                resourceType = "video";
            } else if (extension != null && (extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mov") || extension.equalsIgnoreCase("avi"))) {
                 resourceType = "video";
            }
             log.info("Determined resource type: {}", resourceType);

            Map uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap(
                    "public_id", publicValue,
                    "resource_type", resourceType
                    // Add other Cloudinary options here if needed, e.g., for video transformations
            ));

            // Cloudinary upload successful, get the URL
             if (uploadResult.containsKey("url")) {
                 fileUrl = (String) uploadResult.get("url");
             } else if (uploadResult.containsKey("secure_url")) { // Prefer secure URL if available
                 fileUrl = (String) uploadResult.get("secure_url");
             } else {
                 log.error("Cloudinary upload result did not contain 'url' or 'secure_url' for file: {}", originalFilename);
                 throw new IOException("Cloudinary upload failed: Missing URL in response.");
             }

            log.info("Successfully uploaded file. URL: {}", fileUrl);

        } catch (IOException e) {
            log.error("Cloudinary upload failed for file: {}", originalFilename, e);
            throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }

        return fileUrl;
    }

    // Tạo một giá trị public_id duy nhất để nhận diện tệp trên Cloudinary.
    public String generatePublicValue(String originalName){
        String fileName = getFileNameWithoutExtension(originalName);
        // Ensure public_id is URL-safe and does not contain characters problematic for Cloudinary URLs
        String safeFileName = fileName.replaceAll("[^a-zA-Z0-9\\-_]+", "_");
        return StringUtils.join(UUID.randomUUID().toString(), "_", safeFileName);
    }

    // Tách tên và phần mở rộng của tệp từ originalName.
    private String getFileNameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}
