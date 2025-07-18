package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadRoot);
    }

    /**
     * Store under uploadRoot/<patientCode>/<originalFilename>
     */
    public Path store(MultipartFile file, String patientId) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IOException("Invalid path sequence in file name: " + originalFilename);
        }

        // Get current date formatted
        String formattedDate = LocalDate.now().toString(); // e.g. 2025-07-16

        // Create subdirectory based on patientId + date
        String subDirName = patientId + "_" + formattedDate;
        Path patientDir = uploadRoot.resolve(subDirName);
        Files.createDirectories(patientDir);

        // Extract base name and extension from original filename
        String baseName = originalFilename;
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            baseName = originalFilename.substring(0, dotIndex);
            extension = originalFilename.substring(dotIndex);
        }

        // New filename: baseName_date.extension
        String newFilename = baseName + "_" + formattedDate + extension;

        // Copy the file
        Path target = patientDir.resolve(newFilename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return target;
    }


}