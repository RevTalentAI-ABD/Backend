package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.recruitment.CandidateRequest;
import com.revtalent.revtalent.dto.recruitment.CandidateResponse;
import com.revtalent.revtalent.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    // ── Add candidate to a job ────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<CandidateResponse> addCandidate(@RequestBody CandidateRequest req) {
        return ResponseEntity.ok(candidateService.addCandidate(req));
    }

    // ── Get all candidates for a specific job ─────────────────────────────────
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<CandidateResponse>> getCandidatesByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(candidateService.getCandidatesByJob(jobId));
    }

    // ── Get all candidates ────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<CandidateResponse>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    // ── Move candidate through pipeline stages ────────────────────────────────
    @PutMapping("/{id}/status")
    public ResponseEntity<CandidateResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(candidateService.updateStatus(id, status));
    }

    // ── Delete candidate ──────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok("Candidate removed successfully");
    }
}