package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.mongo.PerformanceReview;
import com.revtalent.revtalent.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService service;

    @PostMapping
    public ResponseEntity<PerformanceReview> create(@RequestBody PerformanceReview review) {
        return ResponseEntity.ok(service.create(review));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceReview> update(
            @PathVariable String id,
            @RequestBody PerformanceReview review) {
        return ResponseEntity.ok(service.update(id, review));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PerformanceReview>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<List<PerformanceReview>> getByReviewer(@PathVariable Long reviewerId) {
        return ResponseEntity.ok(service.getByReviewer(reviewerId));
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<PerformanceReview> acknowledge(@PathVariable String id) {
        return ResponseEntity.ok(service.acknowledge(id));
    }
}