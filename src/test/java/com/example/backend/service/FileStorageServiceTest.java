package com.example.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir Path tempDir;

    @Test
    void store_copiesFileToPatientDirectory() throws Exception {
        FileStorageService svc = new FileStorageService(tempDir.toString());
        svc.init();
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());

        Path stored = svc.store(file, "p1");

        assertTrue(Files.exists(stored));
        assertArrayEquals("data".getBytes(), Files.readAllBytes(stored));
    }

    @Test
    void store_rejectsPathTraversal() throws Exception {
        FileStorageService svc = new FileStorageService(tempDir.toString());
        svc.init();
        MockMultipartFile file = new MockMultipartFile("file", "../evil.txt", "text/plain", "x".getBytes());

        assertThrows(IOException.class, () -> svc.store(file, "p1"));
    }
}
