package com.example.backend.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import java.io.File;

public class AIIntegrationServiceTest {

    @Test
    public void testFindPatientFilePath_Found() {
        String patientId = "519d83aa-9c3c-49a2-9399-41910040e496";

        File mockFolder = mock(File.class);
        File mockFile = mock(File.class);
        File[] mockFiles = new File[] { mockFile };

        when(mockFolder.listFiles()).thenReturn(mockFiles);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.getName()).thenReturn(patientId + "-patient1.json");
        when(mockFile.getAbsolutePath()).thenReturn("/mock/path/dataJson/" + patientId + "-patient1.json");

        AIIntegrationService service = new AIIntegrationService() {
            @Override
            protected File getDataFolder() {
                return mockFolder;
            }
        };

        String path = service.findPatientFilePath(patientId);

        assertNotNull(path);
        assertEquals("/mock/path/dataJson/" + patientId + "-patient1.json", path);
    }

    @Test
    public void testFindPatientFilePath_NotFound() {
        String patientId = "notfound";

        File mockFolder = mock(File.class);
        when(mockFolder.listFiles()).thenReturn(new File[0]);

        AIIntegrationService service = new AIIntegrationService() {
            @Override
            protected File getDataFolder() {
                return mockFolder;
            }
        };

        String path = service.findPatientFilePath(patientId);

        assertNull(path);
    }
}
