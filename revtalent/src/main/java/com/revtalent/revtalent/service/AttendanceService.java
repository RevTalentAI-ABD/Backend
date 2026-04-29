package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.dto.attendance.AttendanceSummaryResponse;
import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    // ✅ Mapper
    private AttendanceResponse mapToResponse(Attendance a) {
        AttendanceResponse res = new AttendanceResponse();
        res.setId(a.getId());
        res.setEmployeeId(a.getEmployee().getId());
        res.setEmployeeName(a.getEmployee().getUser().getName());         // ✅ name not username
        res.setEmployeeCode(a.getEmployee().getEmployeeCode());
        res.setDepartmentName(a.getEmployee().getDepartment().getName());
        res.setWorkDate(a.getWorkDate());
        res.setCheckIn(a.getCheckIn());
        res.setCheckOut(a.getCheckOut());
        res.setDurationMin(a.getDurationMin());
        res.setAttendanceType(a.getAttendanceType());
        res.setRegularized(a.isRegularized());
        res.setNotes(a.getNotes());
        res.setStatus(a.getStatus());
        return res;
    }

    // ✅ Check In
    public Attendance checkIn(Long employeeId) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now();

        if (attendanceRepo.findByEmployeeAndWorkDate(emp, today).isPresent()) {
            throw new RuntimeException("Already checked in today");
        }

        Attendance attendance = Attendance.builder()
                .employee(emp)
                .workDate(today)
                .checkIn(LocalDateTime.now())
                .attendanceType(Attendance.AttendanceType.WFO)
                .build();

        return attendanceRepo.save(attendance);
    }

    // ✅ Check Out
    public Attendance checkOut(Long employeeId) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Attendance attendance = attendanceRepo
                .findByEmployeeAndWorkDate(emp, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No check-in found"));

        attendance.setCheckOut(LocalDateTime.now());
        return attendanceRepo.save(attendance);
    }

    // ✅ Get All
    public List<AttendanceResponse> getAll() {
        return attendanceRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get Summary by Date Range
    public AttendanceSummaryResponse getSummary(LocalDate from, LocalDate to) {

        long totalEmployees = employeeRepo.count();

        long present = attendanceRepo.countByStatusAndWorkDateBetween(
                Attendance.Status.PRESENT, from, to);

        long absent = attendanceRepo.countByStatusAndWorkDateBetween(
                Attendance.Status.ABSENT, from, to);

        long wfh = attendanceRepo.countByStatusAndWorkDateBetween(
                Attendance.Status.WFH, from, to);

        long onLeave = attendanceRepo.countByStatusAndWorkDateBetween(
                Attendance.Status.ON_LEAVE, from, to);

        long field = attendanceRepo.countByAttendanceTypeAndWorkDateBetween(
                Attendance.AttendanceType.FIELD, from, to);

        return new AttendanceSummaryResponse(totalEmployees, present, absent, wfh, onLeave, field);
    }
}