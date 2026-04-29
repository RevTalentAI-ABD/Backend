package com.revtalent.revtalent.dto.attendance;

import com.revtalent.revtalent.model.Attendance.AttendanceType;
import com.revtalent.revtalent.model.Attendance.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;      // from User
    private String employeeCode;      // from Employee
    private String departmentName;    // from Department
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer durationMin;
    private AttendanceType attendanceType;
    private boolean isRegularized;
    private String notes;
    private Status status;
}