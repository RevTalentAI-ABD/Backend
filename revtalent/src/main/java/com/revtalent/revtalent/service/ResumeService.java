package com.revtalent.revtalent.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResumeService {

    public Map<String, Object> analyzeResume(MultipartFile file) {

        Map<String, Object> result = new HashMap<>();

        String filename = file.getOriginalFilename();

        // 🔥 Simple scoring logic (you can improve later)
        int score = 0;

        if (filename != null && filename.toLowerCase().contains("java")) score += 30;
        if (filename != null && filename.toLowerCase().contains("spring")) score += 30;
        if (filename != null && filename.toLowerCase().contains("react")) score += 20;
        if (filename != null && filename.toLowerCase().contains("ml")) score += 20;

        result.put("fileName", filename);
        result.put("score", score);
        result.put("status", score > 60 ? "Good Resume" : "Needs Improvement");

        return result;
    }
}