package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final ManagerService managerService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return managerService.getDashboard();
    }

    @GetMapping("/activity")
    public List<Map<String, Object>> getActivity() {
        return managerService.getActivity();
    }

    @GetMapping("/profile")
    public String profile() {
        return "Manager profile";
    }
}