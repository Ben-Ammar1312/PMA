package com.example.backend.service;

import com.example.backend.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize upload root", e);
        }
    }


    /**
     * Store under uploadRoot/<patientId>_<date>/logicalName[_counter]_<date>.<ext>
     */

    public Path store(MultipartFile file,
                      String patientId,
                      String logicalName,
                      Integer counter) {

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new FileStorageException(
                    "Invalid path sequence in file name: " + originalFilename);
        }

        Path patientDir = resolvePatientDir(patientId);

        // build filename
        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot != -1) {
            ext = originalFilename.substring(dot);
        }
        StringBuilder name = new StringBuilder(logicalName);
        if (counter != null) name.append('_').append(counter);
        name.append('_').append(LocalDate.now())
                .append(ext);


        Path target = patientDir.resolve(name.toString());

        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } catch (IOException e) {
            throw new FileStorageException(
                    "Failed to store file " + originalFilename + " for patient " + patientId, e);
        }
    }




    /**
     * Scan *all* patientId_<date> folders to find the highest
     * logicalName{N} and return next index = max+1
     */
    public int nextIndex(String patientId, String logicalName) {
        Pattern p = Pattern.compile(Pattern.quote(logicalName) + "_(\\d+)_.*",
                Pattern.CASE_INSENSITIVE);

        try (Stream<Path> dirs = Files.list(uploadRoot)) {
            // only directories for this patientId_
            Optional<Integer> max = dirs
                    .filter(Files::isDirectory)
                    .filter(d -> d.getFileName().toString().startsWith(patientId + "_"))
                    .flatMap(dir -> {
                        try {
                            return Files.list(dir);
                        } catch (IOException e) {
                            return Stream.empty();
                        }
                    })
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(p::matcher)
                    .filter(Matcher::matches)
                    .map(m -> Integer.parseInt(m.group(1)))
                    .max(Comparator.naturalOrder());

            return max.orElse(0) + 1;

        } catch (IOException e) {
            throw new FileStorageException(
                    "Could not list directories under " + uploadRoot, e);
        }
    }
    /**
     * Find an existing patientId_<date> folder if present, otherwise
     * create a new one for today.
     */
    private Path resolvePatientDir(String patientId) {
        try (Stream<Path> dirs = Files.list(uploadRoot)) {
            // pick the first matching existing folder
            Optional<Path> existing = dirs
                    .filter(Files::isDirectory)
                    .filter(d -> d.getFileName().toString().startsWith(patientId + "_"))
                    .findFirst();

            if (existing.isPresent()) {
                return existing.get();
            }

            // create new if none
            Path fresh = uploadRoot.resolve(patientId + "_" + LocalDate.now());
            Files.createDirectories(fresh);
            return fresh;

        } catch (IOException e) {
            throw new FileStorageException(
                    "Could not resolve or create directory for patient " + patientId, e);
        }
    }



}