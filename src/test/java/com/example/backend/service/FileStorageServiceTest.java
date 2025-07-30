package com.example.backend.service;

import com.example.backend.exception.FileStorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir Path tempDir;

    @Test
    void store_copiesFileToPatientDirectory() throws Exception {
        FileStorageService svc = new FileStorageService(tempDir.toString());
        svc.init();

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "data".getBytes()
        );

        // service signature = store(file, patientId, logicalName, counter)
        Path stored = svc.store(file, "p1", "test", null);

        assertTrue(Files.exists(stored));
        assertArrayEquals("data".getBytes(), Files.readAllBytes(stored));
        // folder should start with p1_
        assertTrue(stored.getParent().getFileName().toString().startsWith("p1_"));
    }

    @Test
    void store_rejectsPathTraversal() {
        FileStorageService svc = new FileStorageService(tempDir.toString());
        svc.init();

        MockMultipartFile file = new MockMultipartFile(
                "file", "../evil.txt", "text/plain", "x".getBytes()
        );

        assertThrows(FileStorageException.class,
                () -> svc.store(file, "p1", "evil", null));
    }
}