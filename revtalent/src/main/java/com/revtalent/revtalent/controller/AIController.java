package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private OllamaService ollamaService;

    // 🧠 HR Q&A
    @PostMapping("/ask")
    public String askHR(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        return ollamaService.askHR(question);
    }

    // 📄 Resume Screening
    @PostMapping("/screen-resume")
    public String screenResume(@RequestBody Map<String, String> body) {
        String resume = body.get("resume");
        String job    = body.get("job");
        return ollamaService.screenResume(resume, job);
    }

    // 📊 Performance Summary
    @PostMapping("/performance")
    public String performance(@RequestBody Map<String, String> body) {
        String history = body.get("history");
        return ollamaService.performanceSummary(history);
    }

    // 📝 Policy Generator
    @PostMapping("/generate-policy")
    public String generatePolicy(@RequestBody Map<String, String> body) {
        String topic = body.get("topic");
        return ollamaService.generatePolicy(topic);
    }
}