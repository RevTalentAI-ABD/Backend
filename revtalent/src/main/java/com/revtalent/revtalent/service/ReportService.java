package com.revtalent.revtalent.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    public Map<String, Object> getTeamSummary() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalEmployees", 50);
        data.put("activeLeaves", 5);
        data.put("attendanceRate", "92%");
        return data;
    }
}