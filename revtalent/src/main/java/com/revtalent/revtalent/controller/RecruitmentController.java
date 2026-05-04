package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.recruitment.JobPostingResponse;
import com.revtalent.revtalent.dto.recruitment.JobRequest;
import com.revtalent.revtalent.model.JobPosting;
import com.revtalent.revtalent.repository.JobPostingRepository;
import com.revtalent.revtalent.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService service;
    private final JobPostingRepository jobPostingRepository;

    @GetMapping("/jobs")
    public ResponseEntity<List<JobPostingResponse>> jobs() {
        return ResponseEntity.ok(service.getAllJobs());
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobPostingResponse> create(@RequestBody JobRequest req) {
        return ResponseEntity.ok(service.createJob(req));
    }

    @PutMapping("/jobs/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus(JobPosting.Status.valueOf(status));

        jobPostingRepository.save(job);

        return ResponseEntity.ok("Status updated");
    }
}


