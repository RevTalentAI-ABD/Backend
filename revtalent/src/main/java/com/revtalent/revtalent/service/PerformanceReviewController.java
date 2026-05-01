package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.mongo.PerformanceReview;
import com.revtalent.revtalent.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService service;

    @PostMapping
    public PerformanceReview create(@RequestBody PerformanceReview review) {
        return service.create(review);
    }

    @GetMapping("/{employeeId}")
    public List<PerformanceReview> getByEmployee(@PathVariable Long employeeId) {
        return service.getByEmployee(employeeId);
    }
}