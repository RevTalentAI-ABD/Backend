package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.mongo.PerformanceReview;
import com.revtalent.revtalent.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

    private final PerformanceReviewRepository repository;

    public PerformanceReview create(PerformanceReview review) {
        return repository.save(review);
    }

    public List<PerformanceReview> getByEmployee(Long employeeId) {
        return repository.findByEmployeeId(employeeId);
    }
}