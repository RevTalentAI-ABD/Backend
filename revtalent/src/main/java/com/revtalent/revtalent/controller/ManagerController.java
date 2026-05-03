package com.revtalent.revtalent.controller;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.UserRepository;
import com.revtalent.revtalent.service.DashboardService;
import java.security.Principal;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin("*")
public class ManagerController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired                        // ← add this
    private UserRepository userRepo;

    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}