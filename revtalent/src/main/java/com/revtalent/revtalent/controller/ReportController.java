package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.ReportService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/team-summary")
    public ResponseEntity<?> getTeamSummary() {
        return ResponseEntity.ok(reportService.getTeamSummary());
    }
}