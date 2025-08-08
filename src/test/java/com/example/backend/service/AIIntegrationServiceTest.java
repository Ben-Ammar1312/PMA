package com.example.backend.service;

import com.example.backend.exception.AIServiceException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.FertilityRecord;
import com.example.backend.model.requests.SummaryResponse;
import com.example.backend.repository.FertilityRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIIntegrationServiceTest {

    @Mock FertilityRecordRepository repo;
    @Mock RestTemplate restTemplate;

    // helper to build a service with injected deps and a fixed path
    private AIIntegrationService svcWithPath(String fixedPath) {
        AIIntegrationService s = new AIIntegrationService(repo) {
            @Override public String findPatientFilePath(String id) { return fixedPath; }
            @Override protected File getDataFolder() { return new File("unused"); }
        };
        ReflectionTestUtils.setField(s, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(s, "FAST_API_URL_SUMMARIZE", "http://fake/api");
        return s;
    }

    @BeforeEach
    void silence() {
        // nothing – no spies, no manual reflection here
    }

    /* -------- generateSummary -------- */


    @Test
    void generateSummary_success() {
        AIIntegrationService s = svcWithPath("dummy.json");

        Map<String, String> filesMap = Map.of(
                "summary1_path", "path/to/summary1",
                "summary2_path", "path/to/summary2"
        );
        Map<String, Object> summariesMap = Map.of(
                "summary1", "Summary 1 content",
                "summary2", "Summary 2 content"
        );

        Map<String, Object> responseBody = Map.of(
                "message", "ok",
                "status", "success",
                "files", filesMap,
                "summaries", summariesMap
        );

        when(restTemplate.postForEntity(eq("http://fake/api"), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        FertilityRecord rec = new FertilityRecord();
        when(repo.findById("p1")).thenReturn(Optional.of(rec));

        SummaryResponse out = s.generateSummary("p1");

        assertEquals("path/to/summary1", rec.getSummary1Path());
        verify(repo).save(rec);
    }


    @Test
    void generateSummary_emptyResponse_throws() {
        AIIntegrationService s = svcWithPath("dummy.json");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(AIServiceException.class, () -> s.generateSummary("p2"));
    }

    @Test
    void generateSummary_restTemplateThrows_throws() {
        AIIntegrationService s = svcWithPath("dummy.json");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RestClientException("boom"));

        assertThrows(AIServiceException.class, () -> s.generateSummary("p3"));
    }

    @Test
    void generateSummary_fileNotFound_throws() {
        // real behavior: just throw directly
        AIIntegrationService s = new AIIntegrationService(repo);
        ReflectionTestUtils.setField(s, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(s, "FAST_API_URL_SUMMARIZE", "http://fake/api");

        assertThrows(ResourceNotFoundException.class, () -> s.generateSummary("does-not-exist"));
    }

    /* -------- findPatientFilePath -------- */

    @Test
    void findPatientFilePath_found() {
        String id = "519d83aa-9c3c-49a2-9399-41910040e496";
        File folder = mock(File.class);
        File file = mock(File.class);

        when(folder.listFiles()).thenReturn(new File[]{file});
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn(id + "-patient1.json");


        AIIntegrationService s = new AIIntegrationService(repo) {
            @Override protected File getDataFolder() { return folder; }
        };

        String path = s.findPatientFilePath(id);
        assertEquals("dataJson/" + id + "-patient1.json", path);
    }

    @Test
    void findPatientFilePath_notFound_throws() {
        File folder = mock(File.class);
        File file = mock(File.class);
        when(folder.listFiles()).thenReturn(new File[]{file});
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn("someone-else.json");

        AIIntegrationService s = new AIIntegrationService(repo) {
            @Override protected File getDataFolder() { return folder; }
        };

        assertThrows(ResourceNotFoundException.class, () -> s.findPatientFilePath("notfound"));
    }

    @Test
    void findPatientFilePath_emptyFolder_throws() {
        File folder = mock(File.class);
        when(folder.listFiles()).thenReturn(new File[0]);

        AIIntegrationService s = new AIIntegrationService(repo) {
            @Override protected File getDataFolder() { return folder; }
        };

        assertThrows(ResourceNotFoundException.class, () -> s.findPatientFilePath("any"));
    }

    @Test
    void findPatientFilePath_nullFolder_throws() {
        AIIntegrationService s = new AIIntegrationService(repo) {
            @Override protected File getDataFolder() { return null; }
        };
        assertThrows(ResourceNotFoundException.class, () -> s.findPatientFilePath("test"));
    }
}