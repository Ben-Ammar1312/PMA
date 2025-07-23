package com.example.backend.service;
import com.example.backend.model.FertilityRecord;
import com.example.backend.repository.FertilityRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AIIntegrationServiceTest {

    @Mock
    FertilityRecordRepository fertilityRecordRepository;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    AIIntegrationService aiIntegrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Override getDataFolder() to avoid file system dependency in test
        aiIntegrationService = new AIIntegrationService(fertilityRecordRepository) {
            @Override
            protected File getDataFolder() {
                return new File("src/test/resources/dataJson"); // or any dummy folder you create for tests
            }
        };

        // Inject the mocked RestTemplate manually since the service initializes it internally
        // We can use reflection or better to add a constructor or setter to inject RestTemplate in real code
        // For this test, let's mock aiIntegrationService.restTemplate field:
        try {
            java.lang.reflect.Field restTemplateField = AIIntegrationService.class.getDeclaredField("restTemplate");
            restTemplateField.setAccessible(true);
            restTemplateField.set(aiIntegrationService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateSummary_FileNotFound() {
        String patientId = "nonexistent";

        // No file for this patientId, so generateSummary should return error map
        Map<String, Object> result = aiIntegrationService.generateSummary(patientId);
        assertTrue(result.containsKey("error"));
        assertEquals("Patient file not found for ID: " + patientId, result.get("error"));
    }

    @Test
    void testGenerateSummary_SuccessfulSummary() {
        String patientId = "existingPatient";

        // Mock findPatientFilePath to return a dummy file path
        AIIntegrationService spyService = spy(aiIntegrationService);
        doReturn("dataJson/" + patientId + "-data.json").when(spyService).findPatientFilePath(patientId);

        // Mock response from RestTemplate
        Map<String, Object> fakeResponse = Map.of("Overall Summary", "This is a summary.");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(fakeResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class))).thenReturn(responseEntity);

        // Mock fertilityRecordRepository findById
        FertilityRecord record = new FertilityRecord();
        when(fertilityRecordRepository.findById(patientId)).thenReturn(Optional.of(record));

        Map<String, Object> result = spyService.generateSummary(patientId);

        assertNotNull(result);
        assertEquals("This is a summary.", result.get("Overall Summary"));

        // Verify that the summary was set and saved
        assertEquals("This is a summary.", record.getSummary());
        verify(fertilityRecordRepository).save(record);
    }

    @Test
    void testGenerateSummary_EmptyResponse() {
        String patientId = "existingPatient";

        AIIntegrationService spyService = spy(aiIntegrationService);
        doReturn("dataJson/" + patientId + "-data.json").when(spyService).findPatientFilePath(patientId);

        ResponseEntity<Map> emptyResponse = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class))).thenReturn(emptyResponse);

        Map<String, Object> result = spyService.generateSummary(patientId);
        assertTrue(result.containsKey("error"));
        assertEquals("Empty response from summarization service", result.get("error"));
    }

    @Test
    void testGenerateSummary_RestTemplateThrowsException() {
        String patientId = "existingPatient";

        AIIntegrationService spyService = spy(aiIntegrationService);
        doReturn("dataJson/" + patientId + "-data.json").when(spyService).findPatientFilePath(patientId);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        Map<String, Object> result = spyService.generateSummary(patientId);

        assertTrue(result.containsKey("error"));
        assertTrue(((String) result.get("error")).contains("Failed to generate summary"));
    }

    @Test
    public void testFindPatientFilePath_Found() {
        String patientId = "519d83aa-9c3c-49a2-9399-41910040e496";

        File mockFolder = mock(File.class);
        File mockFile = mock(File.class);
        File[] mockFiles = new File[]{mockFile};

        when(mockFolder.listFiles()).thenReturn(mockFiles);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.getName()).thenReturn(patientId + "-patient1.json");
        when(mockFile.getAbsolutePath()).thenReturn("dataJson/" + patientId + "-patient1.json");

        FertilityRecordRepository mockRepo = mock(FertilityRecordRepository.class);

        AIIntegrationService service = new AIIntegrationService(mockRepo) {
            @Override
            protected File getDataFolder() {
                return mockFolder;
            }
        };

        String path = service.findPatientFilePath(patientId);

        assertNotNull(path);
        assertEquals("dataJson/" + patientId + "-patient1.json", path);
    }

    @Test
    public void testFindPatientFilePath_NotFound() {
        String patientId = "notfound";

        File mockFolder = mock(File.class);
        File mockFile = mock(File.class);
        File[] mockFiles = new File[]{mockFile};

        when(mockFolder.listFiles()).thenReturn(mockFiles);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.getName()).thenReturn("some-other-patient.json");

        FertilityRecordRepository mockRepo = mock(FertilityRecordRepository.class);

        AIIntegrationService service = new AIIntegrationService(mockRepo) {
            @Override
            protected File getDataFolder() {
                return mockFolder;
            }
        };

        String path = service.findPatientFilePath(patientId);

        assertNull(path);
    }

    @Test
    public void testFindPatientFilePath_EmptyFolder() {
        String patientId = "anything";

        File mockFolder = mock(File.class);
        when(mockFolder.listFiles()).thenReturn(new File[0]);

        FertilityRecordRepository mockRepo = mock(FertilityRecordRepository.class);

        AIIntegrationService service = new AIIntegrationService(mockRepo) {
            @Override
            protected File getDataFolder() {
                return mockFolder;
            }
        };

        String path = service.findPatientFilePath(patientId);

        assertNull(path);
    }

    @Test
    public void testFindPatientFilePath_NullFolder() {
        FertilityRecordRepository mockRepo = mock(FertilityRecordRepository.class);

        AIIntegrationService service = new AIIntegrationService(mockRepo) {
            @Override
            protected File getDataFolder() {
                return null;
            }
        };

        String path = service.findPatientFilePath("test");
        assertNull(path);
    }
}
