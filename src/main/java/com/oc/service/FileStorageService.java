package com.oc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Store a file and return the filename
     *
     * @param file The file to store
     * @return The filename of the stored file
     * @throws IOException If an error occurs during file storage
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique filename to prevent overwriting existing files
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + fileExtension;

        // Copy the file to the upload directory
        Path targetLocation = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "http://localhost:3001/api/rentals/files/"+filename;
    }
    
    /**
     * Load a file as a Resource by its filename
     *
     * @param filename The name of the file to load
     * @return The file as a Resource
     * @throws IOException If the file cannot be loaded
     */
    public Resource loadFileAsResource(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists()) {
            return resource;
        } else {
            throw new IOException("File not found: " + filename);
        }
    }
    
    /**
     * Get the upload directory path
     *
     * @return The path to the upload directory
     */
    public String getUploadDir() {
        return uploadDir;
    }
}
