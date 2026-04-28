package com.revtalent.revtalent.dto;

import com.revtalent.revtalent.model.Attendance;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceDTO {

    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Attendance.AttendanceType attendanceType; // WFO, WFH, FIELD
    private Attendance.Status status;                 // PRESENT, ABSENT, WFH, ON_LEAVE
    private String notes;
}