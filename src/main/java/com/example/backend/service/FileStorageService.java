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
    public Path store(MultipartFile file, String patientId, String logicalName,
                      Integer counter ) throws IOException {
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

        String extension  = "";
        int dotIndex      = file.getOriginalFilename().lastIndexOf('.');
        if (dotIndex != -1) {
            extension = file.getOriginalFilename().substring(dotIndex); // includes the dot
        }

        StringBuilder sb = new StringBuilder();
        sb.append(logicalName);
        if (counter != null) sb.append('_')
                .append(counter);
        sb.append('_').append(formattedDate)
                .append(extension);

        Path target = patientDir.resolve(sb.toString());
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target;
    }

    /**
     * Back-compat helper: keep the old signature so existing tests (and any
     * other callers you forgot about) still compile.
     * <p>
     * Uses the original file’s base name as the logical name and no counter.
     */
    public Path store(MultipartFile file, String patientId) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IOException("Invalid path sequence in file name: " + originalFilename);
        }

        // derive logicalName = base part of the original file name
        String logicalName = originalFilename;
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            logicalName = originalFilename.substring(0, dotIndex);
        }

        // delegate to the new method
        return store(file, patientId, logicalName, null);
    }


}