package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.AttendanceDTO;
import com.revtalent.revtalent.dto.AttendanceResponseDTO;
import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    // ── Helper ──────────────────────────────────────────────────────────────

    private Attendance fetchAttendanceEntity(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found: " + attendanceId));
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

    // ── Employee methods ────────────────────────────────────────────────────

    public List<AttendanceResponseDTO> getByEmployee(Long empId) {
        return attendanceRepository.findByEmployee_IdOrderByWorkDateDesc(empId)
                .stream()
                .map(AttendanceResponseDTO::from)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponseDTO> getByEmployeeAndDateRange(Long empId, LocalDate from, LocalDate to) {
        return attendanceRepository
                .findByEmployee_IdAndWorkDateBetweenOrderByWorkDateDesc(empId, from, to)
                .stream()
                .map(AttendanceResponseDTO::from)
                .collect(Collectors.toList());
    }

    public AttendanceResponseDTO getByEmployeeAndDate(Long empId, LocalDate date) {
        Attendance a = attendanceRepository.findByEmployee_IdAndWorkDate(empId, date)
                .orElseThrow(() -> new RuntimeException(
                        "No attendance record for employee " + empId + " on " + date));
        return AttendanceResponseDTO.from(a);
    }

    public List<AttendanceResponseDTO> getByDate(LocalDate date) {
        return attendanceRepository.findByWorkDate(date)
                .stream()
                .map(AttendanceResponseDTO::from)
                .collect(Collectors.toList());
    }

    public AttendanceResponseDTO checkIn(Long empId, AttendanceDTO dto) {
        if (attendanceRepository.findByEmployee_IdAndWorkDate(empId, LocalDate.now()).isPresent()) {
            throw new RuntimeException("Already checked in today");
        }

        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        Attendance attendance = Attendance.builder()
                .employee(emp)
                .workDate(LocalDate.now())
                .checkIn(LocalDateTime.now())
                .attendanceType(dto.getAttendanceType() != null
                        ? dto.getAttendanceType()
                        : Attendance.AttendanceType.WFO)
                .status(Attendance.Status.PRESENT)
                .notes(dto.getNotes())
                .build();

        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public AttendanceResponseDTO checkOut(Long empId) {
        Attendance attendance = attendanceRepository
                .findByEmployee_IdAndWorkDate(empId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No check-in found for today"));

        if (attendance.getCheckOut() != null) {
            throw new RuntimeException("Already checked out today");
        }

        attendance.setCheckOut(LocalDateTime.now());
        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public AttendanceResponseDTO save(Long empId, AttendanceDTO dto) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        Attendance attendance = attendanceRepository
                .findByEmployee_IdAndWorkDate(empId, dto.getWorkDate())
                .orElse(new Attendance());

        attendance.setEmployee(emp);
        attendance.setWorkDate(dto.getWorkDate());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setAttendanceType(dto.getAttendanceType() != null
                ? dto.getAttendanceType()
                : Attendance.AttendanceType.WFO);
        attendance.setStatus(dto.getStatus() != null
                ? dto.getStatus()
                : Attendance.Status.PRESENT);
        attendance.setNotes(dto.getNotes());

        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public AttendanceResponseDTO regularize(Long attendanceId, AttendanceDTO dto) {
        Attendance attendance = fetchAttendanceEntity(attendanceId);
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setNotes(dto.getNotes());
        attendance.setRegularized(true);
        return AttendanceResponseDTO.from(attendanceRepository.save(attendance));
    }

    public int getPresentCount(Long empId, LocalDate from, LocalDate to) {
        return attendanceRepository.countByEmployee_IdAndWorkDateBetweenAndStatus(
                empId, from, to, Attendance.Status.PRESENT);
    }

    public void delete(Long attendanceId) {
        if (!attendanceRepository.existsById(attendanceId)) {
            throw new RuntimeException("Attendance record not found: " + attendanceId);
        }
        attendanceRepository.deleteById(attendanceId);
    }

    // ── Manager methods ─────────────────────────────────────────────────────

    public List<AttendanceResponse> getAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
            String empName = a.getEmployee() != null && a.getEmployee().getUser() != null
                    ? a.getEmployee().getUser().getUsername() : "N/A";
            String dept    = a.getEmployee() != null && a.getEmployee().getDepartment() != null
                    ? a.getEmployee().getDepartment().getName() : "N/A";
            Long empId     = a.getEmployee() != null ? a.getEmployee().getId() : null;

            csv.append(a.getId()).append(",")
                    .append(empId).append(",")
                    .append(empName).append(",")
                    .append(dept).append(",")
                    .append(a.getWorkDate()).append(",")
                    .append(a.getCheckIn()    != null ? a.getCheckIn()    : "").append(",")
                    .append(a.getCheckOut()   != null ? a.getCheckOut()   : "").append(",")
                    .append(a.getDurationMin()!= null ? a.getDurationMin(): "").append(",")
                    .append(a.getAttendanceType()).append(",")
                    .append(a.getStatus()).append(",")
                    .append(a.isRegularized()).append(",")
                    .append(a.getNotes()      != null ? a.getNotes()      : "")
                    .append("\n");
        });

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}