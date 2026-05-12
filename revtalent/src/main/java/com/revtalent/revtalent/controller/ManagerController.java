package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.DashboardService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin("*")
public class ManagerController {

    @Autowired private DashboardService dashboardService;

    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }


}