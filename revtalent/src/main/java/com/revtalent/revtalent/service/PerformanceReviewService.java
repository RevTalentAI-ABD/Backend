package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.mongo.PerformanceReview;
import com.revtalent.revtalent.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

    private final PerformanceReviewRepository repository;

    public PerformanceReview create(PerformanceReview review) {
        review.setOverallRating(computeAverage(review));
        review.setStatus(review.getStatus() != null ? review.getStatus() : "SUBMITTED");
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        return repository.save(review);
    }

    public PerformanceReview update(String id, PerformanceReview updated) {
        PerformanceReview existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found: " + id));
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(LocalDateTime.now());
        updated.setOverallRating(computeAverage(updated));
        return repository.save(updated);
    }

    public List<PerformanceReview> getByEmployee(Long employeeId) {
        return repository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    public List<PerformanceReview> getByReviewer(Long reviewerId) {
        return repository.findByReviewerIdOrderByCreatedAtDesc(reviewerId);
    }

    public PerformanceReview acknowledge(String id) {
        PerformanceReview review = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found: " + id));
        review.setStatus("ACKNOWLEDGED");
        review.setUpdatedAt(LocalDateTime.now());
        return repository.save(review);
    }

    private double computeAverage(PerformanceReview r) {
        int[] scores = {
                r.getTechnicalSkills() != null ? r.getTechnicalSkills() : 0,
                r.getCommunication()   != null ? r.getCommunication()   : 0,
                r.getTeamwork()        != null ? r.getTeamwork()        : 0,
                r.getProblemSolving()  != null ? r.getProblemSolving()  : 0,
                r.getLeadership()      != null ? r.getLeadership()      : 0,
                r.getPunctuality()     != null ? r.getPunctuality()     : 0,
        };
        OptionalDouble avg = java.util.Arrays.stream(scores)
                .filter(s -> s > 0).average();
        return avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
    }
}