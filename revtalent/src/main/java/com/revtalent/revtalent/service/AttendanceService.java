package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> getAttendance() {
        return attendanceRepository.findAll();
    }

    public List<Map<String, Object>> getAttendanceSummary() {
        return List.of(
                Map.of("week", "Week 1", "present", 5, "absent", 1, "leave", 1),
                Map.of("week", "Week 2", "present", 6, "absent", 1, "leave", 0)
        );
    }
}