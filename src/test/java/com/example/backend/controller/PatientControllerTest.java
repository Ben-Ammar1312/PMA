package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc
@Import({ FileStorageService.class })
class PatientControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockitoBean private FertilityRecordService fertilityRecordService;
    @MockitoSpyBean private FileStorageService fileStorageService;

    private final java.util.List<Path> createdFiles = new java.util.ArrayList<>();

    @AfterEach
    void removeCreatedFiles() throws IOException {
        for (Path p : createdFiles) {
            Files.deleteIfExists(p);
        }
        Path parentDir = Paths.get("uploads");
        // delete dummyUser_date directory if empty
        String today = java.time.LocalDate.now().toString();
        Path userDir = parentDir.resolve("dummyUser_" + today);
        if (Files.exists(userDir) && Files.list(userDir).findAny().isEmpty()) {
            Files.delete(userDir);
        }
    }


    @Test
    void whenPostingRecordAndFiles_thenReturns201AndFilesOnDisk() throws Exception {
        // stub DB call
        given(fertilityRecordService.addFertilityRecord(any(FertilityRecord.class)))
                .willAnswer(inv -> {
                    FertilityRecord rec = inv.getArgument(0);
                    rec.setId("dummyUser");
                    return rec;
                });

        FertilityRecord rec = new FertilityRecord();
        String json = mapper.writeValueAsString(rec);

        MockMultipartFile recordPart = new MockMultipartFile(
                "record",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );

        MockMultipartFile file1 = new MockMultipartFile(
                "autreDocumentFiles",
                "foo.txt",
                "text/plain",
                "hello".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "autreDocumentFiles",
                "bar.txt",
                "text/plain",
                "world".getBytes()
        );

        mockMvc.perform(multipart("/patient/record")
                        .file(recordPart)
                        .file(file1)
                        .file(file2)
                        .with(req -> { req.setMethod("POST"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(jwt().jwt(t -> t.claim("sub", "dummyUser")))
                )
                .andExpect(status().isCreated());

        // Construct expected directory path with today's date
        String today = java.time.LocalDate.now().toString();
        Path uploadDir = Paths.get("uploads/dummyUser_" + today);

        // Check files inside that directory
        Path p1 = uploadDir.resolve("foo_" + today + ".txt");
        createdFiles.add(p1);
        assertTrue(Files.exists(p1), "foo.txt should exist");
        assertArrayEquals("hello".getBytes(), Files.readAllBytes(p1));

        Path p2 = uploadDir.resolve("bar_" + today + ".txt");
        createdFiles.add(p2);
        assertTrue(Files.exists(p2), "bar.txt should exist");
        assertArrayEquals("world".getBytes(), Files.readAllBytes(p2));
    }

}