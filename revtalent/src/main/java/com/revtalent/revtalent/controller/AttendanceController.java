package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.AttendanceDTO;
import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // GET all records for employee
    @GetMapping("/employee/{empId}")
    public List<Attendance> getByEmployee(@PathVariable Long empId) {
        return attendanceService.getByEmployee(empId);
    }

    // GET by date range
    @GetMapping("/employee/{empId}/range")
    public List<Attendance> getByRange(
            @PathVariable Long empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return attendanceService.getByEmployeeAndDateRange(empId, from, to);
    }

    // GET by specific date
    @GetMapping("/employee/{empId}/date")
    public Attendance getByDate(
            @PathVariable Long empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getByEmployeeAndDate(empId, date);
    }

    // GET all attendance for a date (HR view)
    @GetMapping("/date")
    public List<Attendance> getAllByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getByDate(date);
    }

    // POST check in
    @PostMapping("/employee/{empId}/checkin")
    public Attendance checkIn(@PathVariable Long empId, @RequestBody AttendanceDTO dto) {
        return attendanceService.checkIn(empId, dto);
    }

    // PUT check out
    @PutMapping("/employee/{empId}/checkout")
    public Attendance checkOut(@PathVariable Long empId) {
        return attendanceService.checkOut(empId);
    }

    // POST create/update record (HR/Admin)
    @PostMapping("/employee/{empId}")
    public Attendance save(@PathVariable Long empId, @RequestBody AttendanceDTO dto) {
        return attendanceService.save(empId, dto);
    }

    // PUT regularize
    @PutMapping("/{attendanceId}/regularize")
    public Attendance regularize(@PathVariable Long attendanceId, @RequestBody AttendanceDTO dto) {
        return attendanceService.regularize(attendanceId, dto);
    }

    // GET present count for a month
    @GetMapping("/employee/{empId}/present-count")
    public int getPresentCount(
            @PathVariable Long empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return attendanceService.getPresentCount(empId, from, to);
    }

    // DELETE a record
    @DeleteMapping("/{attendanceId}")
    public void delete(@PathVariable Long attendanceId) {
        attendanceService.delete(attendanceId);
    }
}