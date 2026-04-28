package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> getAttendance() {
        return attendanceRepository.findAll();
    }


    public List<Map<String, Object>> getAttendanceSummary() {
        return attendanceRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDate().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    List<Attendance> weekData = entry.getValue();
                    Map<String, Object> m = new HashMap<>();
                    m.put("week", entry.getKey().toString());
                    m.put("present", weekData.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count());
                    m.put("absent",  weekData.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count());
                    m.put("leave",   weekData.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count());
                    return m;
                })
                .collect(Collectors.toList());
    }
}