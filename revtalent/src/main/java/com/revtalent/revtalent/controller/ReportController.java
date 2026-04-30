package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.ManagerService;
import com.revtalent.revtalent.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ManagerService managerService;
    private final ReportService reportService;

    @GetMapping("/productivity")
    public ResponseEntity<List<Map<String, Object>>> productivity() {
        return ResponseEntity.ok(managerService.getProductivity());
    }

    @GetMapping("/attendance")
    public ResponseEntity<Map<String, Object>> attendanceReport() {
        return ResponseEntity.ok(managerService.getAttendanceReport());
    }

    @GetMapping("/team-summary")
    public ResponseEntity<Map<String, Object>> teamSummary() {
        return ResponseEntity.ok(managerService.getTeamSummary());
    }

    @GetMapping("/hr/team-summary")
    public ResponseEntity<?> getTeamSummary() {
        return ResponseEntity.ok(reportService.getTeamSummary());
    }
}