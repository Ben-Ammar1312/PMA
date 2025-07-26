package com.example.backend.service;

import com.example.backend.exception.AIServiceException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.requests.JsonPathRequest;
import com.example.backend.repository.FertilityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AIIntegrationService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${FAST_API_URL}")
    private  String FAST_API_URL;
    private final FertilityRecordRepository fertilityRecordRepository;

    public String findPatientFilePath(String patientId) {
        File folder = getDataFolder();
        if (folder == null) {
            throw new ResourceNotFoundException("/dataJson folder not found");
        }

        File[] files = folder.listFiles();
        if (files == null) throw new ResourceNotFoundException("/dataJson folder empty");

        for (File file : files) {
            if (file.isFile()
                    && file.getName().startsWith(patientId + "-")
                    && file.getName().endsWith(".json")) {
                // Return relative path, as expected by the FastAPI server
                return "dataJson/" + file.getName();
            }
        }
        throw new ResourceNotFoundException("Patient's files not found");
    }



    public Map<String, Object> generateSummary(String patientId) {
        String filePath = findPatientFilePath(patientId);
        JsonPathRequest requestBody = new JsonPathRequest(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonPathRequest> entity = new HttpEntity<>(requestBody, headers);
        Map<String, Object> summaryData;
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(FAST_API_URL, entity, Map.class);
            summaryData = response.getBody();
        } catch (RestClientException e) {
            throw new AIServiceException("Error calling AI service ", e);
        }
        if (summaryData == null) {
            throw new AIServiceException("Empty response from summarization service");
        }
        Object overall = summaryData.get("Overall Summary");
        if (!(overall instanceof String)) {
            throw new AIServiceException("Missing or invalid 'Overall Summary' field");
        }

        fertilityRecordRepository.findById(patientId).ifPresent(record -> {
            record.setSummary((String) overall);
            fertilityRecordRepository.save(record);
        });
        return summaryData;
    }





    protected File getDataFolder() {
        File folder= new File("dataJson");
        return folder;
    }
}
