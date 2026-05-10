package com.revtalent.revtalent.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    private final RestTemplate restTemplate = new RestTemplate();

    private String callOllama(String prompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "phi3");
            body.put("prompt", prompt);
            body.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(OLLAMA_URL, request, Map.class);

            if (response.getBody() != null && response.getBody().get("response") != null) {
                return response.getBody().get("response").toString();
            }

            return "⚠ No response from AI";

        } catch (Exception e) {
            return "❌ AI Error: " + e.getMessage();
        }
    }

    public String askHR(String question) {
        String prompt = """
                You are a professional HR assistant.
                Answer clearly, professionally, and concisely.

                Question:
                """ + question;
        return callOllama(prompt);
    }

    public String screenResume(String resume, String job) {
        String prompt = """
                You are an HR recruiter.
                Compare the resume with job description and:
                - Give match percentage
                - Highlight strengths
                - Highlight gaps
                - Final recommendation

                Resume:
                """ + resume + """

                Job Description:
                """ + job;
        return callOllama(prompt);
    }

    public String performanceSummary(String history) {
        String prompt = """
                You are an HR manager.
                Analyze employee performance data and:
                - Summarize performance
                - Identify strengths
                - Identify weaknesses
                - Suggest improvements

                Data:
                """ + history;
        return callOllama(prompt);
    }

    public String generatePolicy(String topic) {
        String prompt = """
                Generate a professional HR policy for:
                """ + topic + """

                Include:
                - Purpose
                - Scope
                - Rules
                - Compliance
                """;
        return callOllama(prompt);
    }
}