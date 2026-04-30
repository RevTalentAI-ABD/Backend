package com.revtalent.revtalent.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;

import com.revtalent.revtalent.model.Attendance.AttendanceType;
import com.revtalent.revtalent.model.Attendance.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String department;
    private String employeeCode;      // from Employee
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer durationMin;
    private String attendanceType;
    private String status;
    private boolean isRegularized;
    private String notes;
}