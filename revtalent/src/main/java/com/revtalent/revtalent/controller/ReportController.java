package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.ManagerService;
import com.revtalent.revtalent.service.ReportService;
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
    private final ReportService reportService;

    // ✅ Productivity (Manager Service)
    @GetMapping("/productivity")
    public List<Map<String, Object>> getProductivity() {
        return managerService.getProductivity();
    }

    // ✅ Attendance (Manager Service)
    @GetMapping("/attendance")
    public Map<String, Object> getAttendance() {
        return managerService.getAttendanceReport();
    }

    // ✅ Summary
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return reportService.getSummary();
    }

    // ✅ Team Summary
    @GetMapping("/team-summary")
    public Map<String, Object> getTeamSummary() {
        return reportService.getTeamSummary();
    }

    // ✅ All Reports
    @GetMapping("/all")
    public Map<String, Object> getAllReports() {
        return reportService.getAllReports();
    }
}