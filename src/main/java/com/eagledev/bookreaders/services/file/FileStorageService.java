package com.eagledev.bookreaders.services.file;

import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${application.file.upload-dir:./uploads}")
    private String baseUploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private Path rootLocation;

    public static final String AVATAR_URL="AVATAR_URL";
    public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(baseUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    /**
     * Uploads a file to a specific subdirectory.
     * @param subDirectory The folder name (e.g., "users", "books") inside the root uploads dir.
     * @param file The file object.
     * @return The specified endpoint to get file.
     */
    public String uploadFile(String subDirectory, MultipartFile file) throws BadRequestException {
            String fileName = storeFile(subDirectory,file);
        return baseUrl + "/api/v1/file/" + subDirectory + "/" + fileName;
    }

    /**
     * Uploads a file to a specific subdirectory.
     * @param subDirectory The folder name (e.g., "users", "books") inside the root uploads dir.
     * @param file The file object.
     * @return The generated unique filename.
     */
    public String storeFile(String subDirectory, MultipartFile file) throws BadRequestException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file.");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Cannot store file with relative path outside current directory " + originalFilename);
        }

        try {
            Path targetDir = this.rootLocation.resolve(subDirectory);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String fileExtension = getFileExtension(originalFilename);
            String uniqueFileName = UUID.randomUUID().toString() + (fileExtension.isEmpty() ? "" : "." + fileExtension);

            Path targetPath = targetDir.resolve(uniqueFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + originalFilename, ex);
        }
    }

    /**
     * Loads a file as a Resource.
     * @param subDirectory The folder where the file is stored.
     * @param fileName The name of the file.
     */
    public Resource loadFile(String subDirectory, String fileName) {
        try {
            Path filePath = this.rootLocation.resolve(subDirectory).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File", "name", fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    /**
     * Deletes a file.
     */
    public void deleteFile(String subDirectory, String fileName) {
        try {
            Path filePath = this.rootLocation.resolve(subDirectory).resolve(fileName).normalize();
            if (!Files.exists(filePath)) {
                throw new ResourceNotFoundException("File", "name", fileName);
            }
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + fileName, e);
        }
    }

    // --- Helper ---
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}