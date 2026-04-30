package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.AttendanceDTO;
import com.revtalent.revtalent.dto.AttendanceResponseDTO;
import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    private Attendance fetchAttendanceEntity(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found: " + attendanceId));
    }


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
        // Prevent duplicate check-in for same day
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
}