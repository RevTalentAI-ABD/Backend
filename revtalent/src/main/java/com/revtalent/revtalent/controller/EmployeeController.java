package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.CreateEmployeeRequest;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return service.getEmployeeById(id);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return service.getAllEmployees();
    }

    @PostMapping
    public Employee createEmployee(@RequestBody CreateEmployeeRequest request) {
        return service.createEmployee(request);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id,
                                   @RequestBody Employee employee) {
        return service.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
    }

    @GetMapping("/{id}/dashboard-stats")
    public Object getDashboardStats(@PathVariable Long id) {
        return service.getDashboardStats(id);
    }

    @GetMapping("/{id}/schedule")
    public Object getSchedule(@PathVariable Long id) {
        return service.getSchedule(id);
    }

    @GetMapping("/announcements")
    public Object getAnnouncements() {
        return service.getAnnouncements();
    }
}