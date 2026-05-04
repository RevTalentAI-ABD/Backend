package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.OllamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AIController {

    private final OllamaService service;

    // 🔹 1. HR Q&A
    @PostMapping("/ask")
    public String ask(@RequestBody Map<String, String> body) {
        return service.askHR(body.get("question"));
    }

    // 🔹 2. Resume Screening
    @PostMapping("/resume")
    public String screen(@RequestBody Map<String, String> body) {
        return service.screenResume(body.get("resume"), body.get("job"));
    }

    // 🔹 3. Performance Summary
    @PostMapping("/performance")
    public String performance(@RequestBody Map<String, String> body) {
        return service.performanceSummary(body.get("history"));
    }

    // 🔹 4. Policy Generator
    @PostMapping("/policy")
    public String policy(@RequestBody Map<String, String> body) {
        return service.generatePolicy(body.get("topic"));
    }
    @PostMapping("/upload")
    public String uploadResume(@RequestParam("file") MultipartFile file) throws Exception {
        return ollamaService.extractText(file);
    }
}