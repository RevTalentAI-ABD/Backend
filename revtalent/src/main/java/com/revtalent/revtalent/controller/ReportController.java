package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ManagerService managerService;

    @GetMapping("/productivity")
    public List<Map<String, Object>> productivity() {
        return managerService.getProductivity();
    }

    @GetMapping("/attendance")
    public String attendanceReport() {
        return "Attendance report";
    }

    @GetMapping("/team-summary")
    public String teamSummary() {
        return "Team summary";
    }
}