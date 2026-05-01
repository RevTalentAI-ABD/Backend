package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.ScreeningResultDTO;
import com.revtalent.revtalent.model.Candidate;
import com.revtalent.revtalent.model.JobPosting;
import com.revtalent.revtalent.model.mongo.Resume;
import com.revtalent.revtalent.repository.CandidateRepository;
import com.revtalent.revtalent.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ResumeScreeningService {

    private final ResumeRepository resumeRepository;
    private final CandidateRepository candidateRepository;

    // 🔹 Single candidate score
    public double calculateMatchScore(Long candidateId) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (candidate.getResumeMongoId() == null) {
            throw new RuntimeException("Resume not uploaded");
        }

        Resume resume = resumeRepository.findById(candidate.getResumeMongoId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        JobPosting job = candidate.getJobPosting();
        if (job == null || job.getRequirements() == null) {
            throw new RuntimeException("Job requirements not found");
        }

        String resumeText = resume.getParsedText().toLowerCase();
        String reqText = job.getRequirements().toLowerCase();

        List<String> keywords = Arrays.asList(reqText.split(","));

        int matchCount = 0;

        for (String keyword : keywords) {
            if (resumeText.contains(keyword.trim())) {
                matchCount++;
            }
        }

        double score = ((double) matchCount / keywords.size()) * 100;

        return Math.round(score * 100.0) / 100.0;
    }

    // 🔥 Ranking multiple candidates
    public List<ScreeningResultDTO> rankCandidates(Long jobId) {

        // ✅ DB-level filtering (BEST PRACTICE)
        List<Candidate> candidates = candidateRepository.findByJobPosting_Id(jobId);

        List<ScreeningResultDTO> results = new ArrayList<>();

        for (Candidate candidate : candidates) {

            if (candidate.getResumeMongoId() == null) continue;

            double score = calculateMatchScore(candidate.getId());

            results.add(ScreeningResultDTO.builder()
                    .candidateId(candidate.getId())
                    .name(candidate.getName())
                    .score(score)
                    .build());
        }

        // 🔥 Sort descending
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return results;
    }
}