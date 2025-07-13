package com.example.backend.controller;

import com.example.backend.model.Couple;
import com.example.backend.model.FertilityRecord;
import com.example.backend.model.Partner;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
@Import(FileStorageService.class)
class PatientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    // override the FertilityRecordService bean with a Mockito mock
    @MockitoBean
    FertilityRecordService fertilityRecordService;

    // wrap the real FileStorageService in a spy so we still write to disk
    @MockitoSpyBean
    FileStorageService fileStorageService;

    @BeforeEach
    void setUp() throws IOException {
        Path uploads = Paths.get("uploads");
        if (Files.exists(uploads)) {
            Files.walk(uploads)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void whenPostingRecordAndFiles_thenReturns201AndFilesOnDisk() throws Exception {
        // stub out the DB call
        given(fertilityRecordService.addFertilityRecord(any(FertilityRecord.class)))
                .willReturn(new FertilityRecord());

        FertilityRecord rec = new FertilityRecord();
        rec.setCouple(new Couple("COUPLE-123", new Partner(), new Partner()));

        given(fertilityRecordService.addFertilityRecord(any()))
                .willAnswer(invocation -> invocation.getArgument(0));
        String json = mapper.writeValueAsString(rec);

        // two dummy files
        MockMultipartFile recordPart = new MockMultipartFile(
                "record", "", "application/json", json.getBytes());
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "foo.txt", "text/plain", "hello".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "bar.txt", "text/plain", "world".getBytes());

        // perform the request with a fake JWT
        mockMvc.perform(multipart("/patient/record")
                        .file(recordPart)
                        .file(file1)
                        .file(file2)
                        .with(req -> { req.setMethod("POST"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(jwt().jwt(t -> t.claim("sub", "dummyUser")))
                )
                .andExpect(status().isCreated());

        // verify the files landed on disk
        Path p1 = Paths.get("uploads/COUPLE-123/foo.txt");
        assertTrue(Files.exists(p1));
        assertArrayEquals("hello".getBytes(), Files.readAllBytes(p1));

        Path p2 = Paths.get("uploads/COUPLE-123/bar.txt");
        assertTrue(Files.exists(p2));
        assertArrayEquals("world".getBytes(), Files.readAllBytes(p2));
    }
}