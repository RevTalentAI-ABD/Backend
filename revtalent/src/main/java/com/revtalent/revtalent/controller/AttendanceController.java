package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<AttendanceResponse>> getAttendance() {
        return ResponseEntity.ok(attendanceService.getAttendance());
    }

    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(attendanceService.getAttendanceSummary());
    }


    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        byte[] csvData = attendanceService.exportAttendanceAsCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"attendance_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvData.length)
                .body(csvData);
    }
}