package com.example.backend.controller;

import com.example.backend.model.Couple;
import com.example.backend.model.FertilityRecord;
import com.example.backend.model.Partner;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.SequenceGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
@Import({ FileStorageService.class, SequenceGeneratorService.class })
class PatientControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockitoBean private SequenceGeneratorService sequenceGeneratorService;
    @MockitoBean private FertilityRecordService fertilityRecordService;
    @MockitoSpyBean private FileStorageService fileStorageService;

    @BeforeEach
    void cleanUploads() throws IOException {
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
        // 1) stub out the sequence generator
        given(sequenceGeneratorService.getNextSequence("coupleCode"))
                .willReturn(123L);

        // 2) stub out your DB call so saved.getCouple().getCode() survives
        given(fertilityRecordService.addFertilityRecord(any(FertilityRecord.class)))
                .willAnswer(inv -> inv.getArgument(0));

        // 3) build your record JSON
        FertilityRecord rec = new FertilityRecord();
        rec.setCouple(new Couple(null, new Partner(), new Partner()));
        String json = mapper.writeValueAsString(rec);

        MockMultipartFile recordPart = new MockMultipartFile(
                "record",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );

        // 4) two "other" files
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

        // 5) perform the multipart POST
        mockMvc.perform(multipart("/patient/record")
                        .file(recordPart)
                        .file(file1)
                        .file(file2)
                        // multipart builder defaults to GET, so force POST
                        .with(req -> { req.setMethod("POST"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(jwt().jwt(t -> t.claim("sub", "dummyUser")))
                )
                .andExpect(status().isCreated());

        // 6) assert files landed under uploads/123/
        Path p1 = Paths.get("uploads/123/foo.txt");
        assertTrue(Files.exists(p1), "foo.txt should exist");
        assertArrayEquals("hello".getBytes(), Files.readAllBytes(p1));

        Path p2 = Paths.get("uploads/123/bar.txt");
        assertTrue(Files.exists(p2), "bar.txt should exist");
        assertArrayEquals("world".getBytes(), Files.readAllBytes(p2));
    }
}