package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<AttendanceResponse> getAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AttendanceResponse toDTO(Attendance a) {
        return AttendanceResponse.builder()
                .id(a.getId())
                .employeeId(a.getEmployee() != null ? a.getEmployee().getId() : null)
                .employeeName(a.getEmployee() != null && a.getEmployee().getUser() != null
                        ? a.getEmployee().getUser().getUsername() : "N/A")
                .department(a.getEmployee() != null && a.getEmployee().getDepartment() != null
                        ? a.getEmployee().getDepartment().getName() : "N/A")
                .workDate(a.getWorkDate())
                .checkIn(a.getCheckIn())
                .checkOut(a.getCheckOut())
                .durationMin(a.getDurationMin())
                .attendanceType(a.getAttendanceType().name())
                .status(a.getStatus().name())
                .isRegularized(a.isRegularized())
                .notes(a.getNotes())
                .build();
    }


    public List<Map<String, Object>> getAttendanceSummary() {
        return attendanceRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getWorkDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    List<Attendance> weekData = entry.getValue();
                    Map<String, Object> m = new HashMap<>();
                    m.put("week",    entry.getKey().toString());
                    m.put("present", weekData.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count());
                    m.put("absent",  weekData.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count());
                    m.put("leave",   weekData.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count());
                    m.put("wfh",     weekData.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count());
                    return m;
                })
                .sorted(Comparator.comparing(m -> m.get("week").toString()))
                .collect(Collectors.toList());
    }
    public byte[] exportAttendanceAsCsv() {
        List<Attendance> records = attendanceRepository.findAll();

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Employee ID,Employee Name,Department,Work Date,Check In,Check Out,Duration (min),Type,Status,Regularized,Notes\n");

        records.forEach(a -> {
            String empName   = a.getEmployee() != null && a.getEmployee().getUser() != null
                    ? a.getEmployee().getUser().getUsername() : "N/A";
            String dept      = a.getEmployee() != null && a.getEmployee().getDepartment() != null
                    ? a.getEmployee().getDepartment().getName() : "N/A";
            Long empId       = a.getEmployee() != null ? a.getEmployee().getId() : null;

            csv.append(a.getId()).append(",")
                    .append(empId).append(",")
                    .append(empName).append(",")
                    .append(dept).append(",")
                    .append(a.getWorkDate()).append(",")
                    .append(a.getCheckIn() != null ? a.getCheckIn() : "").append(",")
                    .append(a.getCheckOut() != null ? a.getCheckOut() : "").append(",")
                    .append(a.getDurationMin() != null ? a.getDurationMin() : "").append(",")
                    .append(a.getAttendanceType()).append(",")
                    .append(a.getStatus()).append(",")
                    .append(a.isRegularized()).append(",")
                    .append(a.getNotes() != null ? a.getNotes() : "")
                    .append("\n");
        });

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}