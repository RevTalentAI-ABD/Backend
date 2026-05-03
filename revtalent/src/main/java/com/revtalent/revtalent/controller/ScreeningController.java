package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.ScreeningResultDTO;
import com.revtalent.revtalent.service.ResumeScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
public class ScreeningController {

    private final ResumeScreeningService screeningService;

    // 🔹 Single candidate score
    @GetMapping("/{candidateId}")
    public double getScore(@PathVariable Long candidateId) {
        return screeningService.calculateMatchScore(candidateId);
    }

    // 🔥 Ranking for HR
    @GetMapping("/job/{jobId}")
    public List<ScreeningResultDTO> rankCandidates(@PathVariable Long jobId) {
        return screeningService.rankCandidates(jobId);
    }
}