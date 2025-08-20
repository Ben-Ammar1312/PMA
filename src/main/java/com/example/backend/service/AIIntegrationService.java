package com.example.backend.service;

import com.example.backend.exception.AIServiceException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.requests.JsonPathRequest;
import com.example.backend.model.requests.SummaryResponse;
import com.example.backend.repository.FertilityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Value("${FAST_API_URL_PROCESS_COMPLEMENTARY}")
    private String FAST_API_URL_PROCESS_COMPLEMENTARY;
    @Value("${FAST_API_URL_DELETE}")
    private String FAST_API_URL_DELETE;
    private final FertilityRecordRepository fertilityRecordRepository;
    private static final Logger log = LoggerFactory.getLogger(AIIntegrationService.class);


    public String findPatientFilePath(String patientId) {
        File folder = getDataFolder();
        if (folder == null) {
            throw new ResourceNotFoundException("/dataJson folder not found");
        }

        File[] files = folder.listFiles();
        if (files == null) throw new ResourceNotFoundException("/dataJson folder empty");

        for (File file : files) {
            if (file.isFile()
                    && file.getName().startsWith(patientId)
                    && file.getName().endsWith(".json")) {
                // Return relative path, as expected by the FastAPI server
                log.info("/app/dataJson/" + file.getName());
                return "/app/dataJson/" + file.getName();
            }
        }
        throw new ResourceNotFoundException("Patient's files not found");
    }



    public SummaryResponse generateSummary(String patientId) {
        String filePath = findPatientFilePath(patientId); // throws if not found
        JsonPathRequest body = new JsonPathRequest(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonPathRequest> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp;
        try {
            resp = restTemplate.postForEntity(FAST_API_URL_SUMMARIZE, entity, Map.class);
        } catch (RestClientException e) {
            log.error("error during call to fastAPI {}", e.getMessage(), e);
            throw new AIServiceException("Error calling summarization service", e);
        }

        Map<String, Object> payload = Optional.ofNullable(resp)
                .map(ResponseEntity::getBody)
                .orElseThrow(() -> new AIServiceException("Empty response from summarization service"));

        // Extract data
        String message = (String) payload.get("message");
        String status = (String) payload.get("status");

        Map<String, String> filesMap = (Map<String, String>) payload.get("files");
        if (filesMap == null) {
            throw new AIServiceException("Missing 'files' in AI service response");
        }

        String summary1Path = filesMap.get("summary1_path");
        String summary2Path = filesMap.get("summary2_path");

        if (summary1Path == null || summary2Path == null) {
            throw new AIServiceException("Missing summary file paths in AI service response");
        }

        Map<String, Object> summariesMap = (Map<String, Object>) payload.get("summaries");

        // Save in DB
        fertilityRecordRepository.findById(patientId).ifPresent(record -> {
            record.setSummary1Path(summary1Path);
            record.setSummary2Path(summary2Path);
            fertilityRecordRepository.save(record);
        });

        return new SummaryResponse(message, filesMap, summariesMap, status);
    }





    public Map<String, Object> processAndIndex(String recordId,
                                               String patientPath,
                                               String questionnaireData) {
        String url = FAST_API_URL_PROCESS;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of(
                "record_id", recordId,
                "patient_path", patientPath,
                "questionnaire_data", questionnaireData
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

    public Map<String, Object> processAndIndexComplementary(String recordId,
                                                            String patientPath) {
        String url = FAST_API_URL_PROCESS_COMPLEMENTARY;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of(
                "record_id", recordId,
                "patient_path", patientPath
        );

        HttpEntity<Map<String, String>> req = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new AIServiceException(
                    "Failed to process-and-index-complementary (status=" + resp.getStatusCode() + ")");
        }
        //noinspection unchecked
        return (Map<String, Object>) resp.getBody();
    }

    public void deletePatientData(String recordId) {
        String url = FAST_API_URL_DELETE + recordId;
        try {
            restTemplate.delete(url);
        } catch (RestClientException e) {
            log.error("error during call to fastAPI {}", e.getMessage(), e);
            throw new AIServiceException("Error calling delete service", e);
        }
    }






    protected File getDataFolder() {
        return new File("/app/dataJson");
    }

}
