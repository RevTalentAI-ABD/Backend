package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.employee.EmployeeResponse;
import com.revtalent.revtalent.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/team")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getTeam() {
        return ResponseEntity.ok(employeeService.getTeam());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> searchTeam(@RequestParam String query) {
        return ResponseEntity.ok(employeeService.searchTeam(query));
    }
}