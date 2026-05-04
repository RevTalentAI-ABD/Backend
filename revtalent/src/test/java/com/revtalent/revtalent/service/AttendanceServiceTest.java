package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.dto.attendance.AttendanceSummaryResponse;
import com.revtalent.revtalent.model.*;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock private AttendanceRepository attendanceRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks private AttendanceService attendanceService;

    private Employee employee;
    private Attendance attendance;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setUser(user);
        employee.setDepartment(department);

        attendance = Attendance.builder()
                .id(1L)
                .employee(employee)
                .workDate(LocalDate.now())
                .checkIn(LocalDateTime.now())
                .attendanceType(Attendance.AttendanceType.WFO)
                .status(Attendance.Status.PRESENT)
                .build();
    }

    @Test
    void checkIn_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByEmployee_IdAndWorkDate(1L, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        assertNotNull(attendance);
        assertEquals(Attendance.AttendanceType.WFO, attendance.getAttendanceType());
    }

    @Test
    void checkOut_success() {
        attendance.setCheckOut(LocalDateTime.now());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        assertNotNull(attendance.getCheckOut());
    }

    @Test
    void getAll_returnsResponseList() {
        when(attendanceRepository.findAll()).thenReturn(List.of(attendance));

        List<AttendanceResponse> result = attendanceService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getSummary_returnsCorrectCounts() {
        LocalDate from = LocalDate.of(2025, 4, 1);
        LocalDate to   = LocalDate.of(2025, 4, 30);

        when(employeeRepository.count()).thenReturn(10L);
        when(attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.PRESENT,  from, to)).thenReturn(8L);
        when(attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.ABSENT,   from, to)).thenReturn(1L);
        when(attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.WFH,      from, to)).thenReturn(1L);
        when(attendanceRepository.countByStatusAndWorkDateBetween(Attendance.Status.ON_LEAVE, from, to)).thenReturn(0L);
        when(attendanceRepository.countByAttendanceTypeAndWorkDateBetween(Attendance.AttendanceType.FIELD, from, to)).thenReturn(0L);

        AttendanceSummaryResponse result = attendanceService.getSummary(from, to);

        assertEquals(10L, result.getTotalEmployees());
        assertEquals(8L,  result.getPresent());
        assertEquals(1L,  result.getAbsent());
    }
}