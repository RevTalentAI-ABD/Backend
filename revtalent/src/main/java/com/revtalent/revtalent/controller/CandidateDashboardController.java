package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.CandidateDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateDashboardController {

    private final CandidateDashboardService dashboardService;

    // GET /api/candidate/profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(dashboardService.getProfile(username));
    }

    // GET /api/candidate/applications
    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(dashboardService.getApplications(username));
    }

    // GET /api/candidate/interviews/upcoming
    @GetMapping("/interviews/upcoming")
    public ResponseEntity<?> getUpcomingInterviews(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(dashboardService.getUpcomingInterviews(username));
    }

    // GET /api/candidate/notifications
    // Candidates don't have notifications yet — returns empty list
    @GetMapping("/notifications")
    public ResponseEntity<List<?>> getNotifications() {
        return ResponseEntity.ok(List.of());
    }
}