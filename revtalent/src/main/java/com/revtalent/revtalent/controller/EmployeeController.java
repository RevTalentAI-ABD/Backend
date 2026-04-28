package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/team")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<Map<String, Object>> getTeam() {
        return employeeService.getTeam();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchTeam(@RequestParam String query) {
        return employeeService.searchTeam(query);
    }
}