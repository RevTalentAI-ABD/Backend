package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.recruitment.CandidateRequest;
import com.revtalent.revtalent.dto.recruitment.CandidateResponse;
import com.revtalent.revtalent.model.Candidate;
import com.revtalent.revtalent.model.JobPosting;
import com.revtalent.revtalent.repository.CandidateRepository;
import com.revtalent.revtalent.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final JobPostingRepository jobPostingRepository;

    // ── Mapper ────────────────────────────────────────────────────────────────
    private CandidateResponse toResponse(Candidate c) {
        return CandidateResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .status(c.getStatus())
                .jobId(c.getJobPosting() != null ? c.getJobPosting().getId() : null)
                .jobTitle(c.getJobPosting() != null ? c.getJobPosting().getTitle() : null)
                .resumeMongoId(c.getResumeMongoId())
                .interviewDate(c.getInterviewDate())
                .offerDate(c.getOfferDate())
                .appliedAt(c.getAppliedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    // ── Add candidate to a job ────────────────────────────────────────────────
    @Transactional
    public CandidateResponse addCandidate(CandidateRequest req) {
        JobPosting job = jobPostingRepository.findById(req.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found: " + req.getJobId()));

        // Prevent duplicate application to same job
        boolean exists = candidateRepository
                .findByEmail(req.getEmail())
                .stream()
                .anyMatch(c -> c.getJobPosting().getId().equals(req.getJobId()));

        if (exists) {
            throw new IllegalArgumentException("Candidate with this email already applied to this job.");
        }

        Candidate candidate = Candidate.builder()
                .jobPosting(job)
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(Candidate.Status.APPLIED)
                .build();

        return toResponse(candidateRepository.save(candidate));
    }

    // ── Get all candidates for a job ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CandidateResponse> getCandidatesByJob(Long jobId) {
        return candidateRepository.findByJobPosting_Id(jobId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get all candidates ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Move candidate through pipeline ───────────────────────────────────────
    @Transactional
    public CandidateResponse updateStatus(Long id, String status) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + id));

        try {
            candidate.setStatus(Candidate.Status.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status +
                    ". Valid values: APPLIED, SCREENING, INTERVIEW, OFFERED, HIRED, REJECTED, WITHDRAWN");
        }

        return toResponse(candidateRepository.save(candidate));
    }

    // ── Delete candidate ──────────────────────────────────────────────────────
    @Transactional
    public void deleteCandidate(Long id) {
        if (!candidateRepository.existsById(id)) {
            throw new RuntimeException("Candidate not found: " + id);
        }
        candidateRepository.deleteById(id);
    }
}