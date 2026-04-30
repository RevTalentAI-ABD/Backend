package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.AttendanceDTO;
import com.revtalent.revtalent.dto.AttendanceResponseDTO;
import com.revtalent.revtalent.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.model.Attendance;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor

@CrossOrigin(origins = "http://localhost:5173")

public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<AttendanceResponseDTO>> getByEmployee(@PathVariable Long empId) {
        return ResponseEntity.ok(attendanceService.getByEmployee(empId));
    }

    @GetMapping("/employee/{empId}/range")
    public ResponseEntity<List<AttendanceResponseDTO>> getByRange(
            @PathVariable Long empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getByEmployeeAndDateRange(empId, from, to));
    }

    @PostMapping("/employee/{empId}/checkin")
    public ResponseEntity<AttendanceResponseDTO> checkIn(
            @PathVariable Long empId,
            @RequestBody AttendanceDTO dto) {
        return ResponseEntity.ok(attendanceService.checkIn(empId, dto));
    }

    @PutMapping("/employee/{empId}/checkout")
    public ResponseEntity<AttendanceResponseDTO> checkOut(@PathVariable Long empId) {
        return ResponseEntity.ok(attendanceService.checkOut(empId));
    }

    @PostMapping("/employee/{empId}")
    public ResponseEntity<AttendanceResponseDTO> save(
            @PathVariable Long empId,
            @RequestBody AttendanceDTO dto) {
        return ResponseEntity.ok(attendanceService.save(empId, dto));
    }

    @PutMapping("/{attendanceId}/regularize")
    public ResponseEntity<AttendanceResponseDTO> regularize(
            @PathVariable Long attendanceId,
            @RequestBody AttendanceDTO dto) {
        return ResponseEntity.ok(attendanceService.regularize(attendanceId, dto));
    }

    @GetMapping("/employee/{empId}/present-count")
    public ResponseEntity<Integer> getPresentCount(
            @PathVariable Long empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getPresentCount(empId, from, to));
    }
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