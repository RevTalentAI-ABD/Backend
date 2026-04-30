package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.EmployeeResponse;
import com.revtalent.revtalent.dto.PatchEmployeeRequest;
import com.revtalent.revtalent.dto.UpdateEmployeeRequest;
import com.revtalent.revtalent.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;

    // ── Employee endpoints ────────────────────────────────────────────────────

    @GetMapping("employees/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("employees/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PatchMapping("/employees/{id}/personal-info")
    public ResponseEntity<EmployeeResponse> patchPersonalInfo(
            @PathVariable Long id,
            @RequestBody PatchEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.patchPersonalInfo(id, request));
    }

    @GetMapping("/employees/{id}/dashboard-stats")
    public ResponseEntity<EmployeeResponse> getDashboardStats(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/employees/{id}/schedule")
    public ResponseEntity<EmployeeResponse> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("employees/announcements")
    public ResponseEntity<List<String>> getAnnouncements() {
        return ResponseEntity.ok(employeeService.getAnnouncements());
    }

    // ── Manager / Team endpoints ──────────────────────────────────────────────

    @GetMapping("/manager/team")
    public ResponseEntity<List<EmployeeResponse>> getTeam() {
        return ResponseEntity.ok(employeeService.getTeam());
    }

    @GetMapping("/manager/search")
    public ResponseEntity<List<EmployeeResponse>> searchTeam(@RequestParam String query) {
        return ResponseEntity.ok(employeeService.searchTeam(query));
    }
}