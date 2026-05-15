package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.ScreeningResultDTO;
import com.revtalent.revtalent.model.Candidate;
import com.revtalent.revtalent.model.JobPosting;
import com.revtalent.revtalent.model.mongo.Resume;
import com.revtalent.revtalent.repository.CandidateRepository;
import com.revtalent.revtalent.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ResumeScreeningService {

    private final ResumeRepository resumeRepository;
    private final CandidateRepository candidateRepository;
    private final OllamaService ollamaService;

    // 🔹 AI Candidate analysis
    public String getAiResumeAnalysis(Long candidateId) {
        Candidate candidate = candidateRepository.findByIdWithJobPosting(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (candidate.getResumeMongoId() == null) {
            return "Resume not uploaded";
        }

        Resume resume = resumeRepository.findById(candidate.getResumeMongoId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        JobPosting job = candidate.getJobPosting();
        if (job == null) {
            return "Job posting not found for this candidate";
        }

        String jobDescription = job.getTitle() + "\n" + job.getDescription() + "\nRequirements: " + job.getRequirements();
        return ollamaService.screenResume(resume.getParsedText(), jobDescription);
    }

    // 🔹 Async background AI processing
    @Async
    @org.springframework.transaction.annotation.Transactional
    public void processCandidateAiScoreAsync(Long candidateId) {
        try {
            Candidate candidate = candidateRepository.findByIdWithJobPosting(candidateId).orElse(null);
            if (candidate == null || candidate.getResumeMongoId() == null || candidate.getJobPosting() == null) {
                return;
            }

            Resume resume = resumeRepository.findById(candidate.getResumeMongoId()).orElse(null);
            if (resume == null || resume.getParsedText() == null) {
                return;
            }

            String aiResponse = getAiResumeAnalysis(candidateId);
            
            // Parse SCORE: XX
            double score = 0.0;
            String summary = aiResponse;
            if (aiResponse.contains("SCORE:")) {
                try {
                    String[] parts = aiResponse.split("SCORE:");
                    if (parts.length > 1) {
                        String afterScore = parts[1].trim();
                        String scoreStr = afterScore.split("[^0-9.]")[0]; // extract the number
                        score = Double.parseDouble(scoreStr);
                        // Optionally strip the score line from summary
                        summary = afterScore.substring(scoreStr.length()).trim();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse AI Score: " + e.getMessage());
                }
            }

            candidate.setAiScore(score);
            candidate.setAiSummary(summary);
            candidateRepository.save(candidate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            double score = candidate.getAiScore() != null ? candidate.getAiScore() : calculateMatchScore(candidate.getId());

            results.add(ScreeningResultDTO.builder()
                    .candidateId(candidate.getId())
                    .name(candidate.getName())
                    .score(score)
                    .aiSummary(candidate.getAiSummary())
                    .build());
        }

        // 🔥 Sort descending
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return results;
    }
}
