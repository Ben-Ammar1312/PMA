package com.example.backend.model.requests;

import java.util.Map;

public record SummaryResponse(
        String message,
        Map<String, String> files,
        Map<String, Object> summaries,
        String status
) {}