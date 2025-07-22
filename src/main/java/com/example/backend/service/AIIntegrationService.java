package com.example.backend.service;

import com.example.backend.model.FertilityRecord;
import com.example.backend.model.requests.JsonPathRequest;
import com.example.backend.repository.FertilityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AIIntegrationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FAST_API_URL = "http://144.91.76.149:8000/summarize_json";
    private final FertilityRecordRepository fertilityRecordRepository;

    public String findPatientFilePath(String patientId) {
        File folder = getDataFolder();
        if (folder == null) {
            return null;
        }

        File[] files = folder.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file.isFile()
                    && file.getName().startsWith(patientId + "-")
                    && file.getName().endsWith(".json")) {
                // Return relative path, as expected by the FastAPI server
                return "dataJson/" + file.getName();
            }
        }
        return null;
    }



    public Map<String, Object> generateSummary(String patientId) {
        String filePath = findPatientFilePath(patientId);
        if (filePath == null) {
            return Map.of("error", "Patient file not found for ID: " + patientId);
        }
        JsonPathRequest requestBody = new JsonPathRequest(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonPathRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(FAST_API_URL, entity, Map.class);
            Map<String, Object> summaryData = response.getBody();

            if (summaryData != null && summaryData.containsKey("summary_fr")) {
                String overallSummary = (String) summaryData.get("summary_fr");

                fertilityRecordRepository.findById(patientId).ifPresent(record -> {
                    record.setSummary(overallSummary);
                    fertilityRecordRepository.save(record);
                });
            }

            return summaryData != null ? summaryData : Map.of("error", "Empty response from summarization service");

        } catch (Exception ex) {
            ex.printStackTrace();
            return Map.of("error", "Failed to generate summary: " + ex.getMessage());
        }
    }




    protected File getDataFolder() {
        File folder= new File("dataJson");
        System.out.println("Folder absolute path: " + folder.getAbsolutePath());
        System.out.println("Folder exists? " + folder.exists());
        System.out.println("Folder is directory? " + folder.isDirectory());
        return folder;
    }
}
