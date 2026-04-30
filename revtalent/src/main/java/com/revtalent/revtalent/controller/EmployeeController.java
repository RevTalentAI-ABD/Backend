package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.CreateEmployeeRequest;
import com.revtalent.revtalent.dto.EmployeeResponse;
import com.revtalent.revtalent.dto.PatchEmployeeRequest;
import com.revtalent.revtalent.dto.UpdateEmployeeRequest;
import com.revtalent.revtalent.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }




    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(service.updateEmployee(id, request));
    }

    // ── PATCH: update only personal info fields ───────────────────────────────
    @PatchMapping("/{id}/personal-info")
    public ResponseEntity<EmployeeResponse> patchPersonalInfo(
            @PathVariable Long id,
            @RequestBody PatchEmployeeRequest request) {
        return ResponseEntity.ok(service.patchPersonalInfo(id, request));
    }
    // ─────────────────────────────────────────────────────────────────────────



    @GetMapping("/{id}/dashboard-stats")
    public ResponseEntity<EmployeeResponse> getDashboardStats(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<EmployeeResponse> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<String>> getAnnouncements() {
        return ResponseEntity.ok(service.getAnnouncements());
    }
}