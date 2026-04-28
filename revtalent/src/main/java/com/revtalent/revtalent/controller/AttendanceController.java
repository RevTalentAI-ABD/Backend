package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public List<Attendance> getAttendance() {
        return attendanceService.getAttendance();
    }

    @GetMapping("/summary")
    public List<Map<String, Object>> getSummary() {
        return attendanceService.getAttendanceSummary();
    }

    @GetMapping("/export")
    public String export() {
        return "Export success";
    }
}