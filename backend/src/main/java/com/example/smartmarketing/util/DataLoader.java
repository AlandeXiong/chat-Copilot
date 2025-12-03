package com.example.smartmarketing.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading mock data from JSON/HTML files in the data/ folder.
 * This allows easy modification of mock responses without recompiling Java code.
 */
@Component
public class DataLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Load JSON file from classpath and parse as JsonNode
     */
    public JsonNode loadJson(String filename) {
        try {
            InputStream inputStream = new ClassPathResource("data/" + filename).getInputStream();
            return objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data file: " + filename, e);
        }
    }

    /**
     * Load text/HTML file from classpath as string
     */
    public String loadText(String filename) {
        try {
            InputStream inputStream = new ClassPathResource("data/" + filename).getInputStream();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load text file: " + filename, e);
        }
    }

    /**
     * Load segment data from segment-data.json
     */
    public JsonNode loadSegmentData() {
        return loadJson("segment-data.json");
    }

    /**
     * Load email template from email-template.html
     */
    public String loadEmailTemplate() {
        return loadText("email-template.html");
    }

    /**
     * Load journey plan from journey-plan.json
     */
    public JsonNode loadJourneyData() {
        return loadJson("journey-plan.json");
    }

    /**
     * Load analytics data from analytics-data.json
     */
    public JsonNode loadAnalyticsData() {
        return loadJson("analytics-data.json");
    }

    /**
     * Load deployment config from deployment-config.json
     */
    public JsonNode loadDeploymentConfig() {
        return loadJson("deployment-config.json");
    }

    /**
     * Load thinking steps for a specific stage
     */
    public JsonNode loadThinkingSteps(String stage) {
        JsonNode allSteps = loadJson("thinking-steps.json");
        return allSteps.path(stage);
    }
}

