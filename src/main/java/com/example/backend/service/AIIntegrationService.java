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
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AIIntegrationService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${FAST_API_URL_SUMMARIZE}")
    private  String FAST_API_URL_SUMMARIZE;
    @Value("${FAST_API_URL_PROCESS}")
    private  String FAST_API_URL_PROCESS;
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
        String filePath = findPatientFilePath(patientId); // now throws ResourceNotFoundException
        JsonPathRequest body = new JsonPathRequest(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonPathRequest> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp;
        try {
            resp = restTemplate.postForEntity(FAST_API_URL_SUMMARIZE, entity, Map.class);
        } catch (RestClientException e) {
            throw new AIServiceException("Error calling summarization service", e);
        }

        Map<String, Object> payload = Optional.ofNullable(resp)
                .map(ResponseEntity::getBody)
                .orElseThrow(() -> new AIServiceException("Empty response from summarization service"));

        Object overall = payload.get("Overall Summary");
        if (!(overall instanceof String s)) {
            throw new AIServiceException("Missing or invalid 'Overall Summary' field");
        }

        fertilityRecordRepository.findById(patientId).ifPresent(r -> {
            r.setSummary(s);
            fertilityRecordRepository.save(r);
        });

        return payload;
    }



    public Map<String, Object> processAndIndex(String recordId, String patientPath) {
        String url = FAST_API_URL_PROCESS ;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the JSON payload exactly as in your spec
        Map<String, String> payload = Map.of(
                "record_id",  recordId,
                "patient_path", patientPath
        );

        HttpEntity<Map<String, String>> req = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new AIServiceException(
                    "Failed to process-and-index (status=" + resp.getStatusCode() + ")");
        }
        //noinspection unchecked
        return (Map<String, Object>) resp.getBody();
    }






    protected File getDataFolder() {
        return new File("dataJson");
    }
}
