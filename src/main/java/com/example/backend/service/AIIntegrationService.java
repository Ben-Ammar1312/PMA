package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class AIIntegrationService {

    public String findPatientFilePath(String patientId) {
        File folder = getDataFolder(); // use method here
        File[] files = folder.listFiles();

        if (files == null) return null;

        for (File file : files) {
            if (file.isFile()
                    && file.getName().startsWith(patientId + "-")
                    && file.getName().endsWith(".json")) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    // Add this method so test subclass can override it
    protected File getDataFolder() {
        return new File("dataJson");
    }
}
