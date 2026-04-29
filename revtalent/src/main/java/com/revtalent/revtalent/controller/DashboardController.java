package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.employee.ManagerProfileResponse;
import com.revtalent.revtalent.service.EmployeeService;
import com.revtalent.revtalent.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final ManagerService managerService;
    private final EmployeeService employeeService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(managerService.getDashboard());
    }

    @GetMapping("/activity")
    public ResponseEntity<List<Map<String, Object>>> getActivity() {
        return ResponseEntity.ok(managerService.getActivity());
    }
    
    @GetMapping("/profile")
    public ResponseEntity<ManagerProfileResponse> profile() {
        return ResponseEntity.ok(employeeService.getManagerProfile(1L));
    }
}