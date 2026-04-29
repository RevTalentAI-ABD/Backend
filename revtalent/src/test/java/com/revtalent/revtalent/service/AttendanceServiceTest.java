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

    @Mock private AttendanceRepository attendanceRepo;
    @Mock private EmployeeRepository employeeRepo;

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
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepo.findByEmployeeAndWorkDate(employee, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(attendanceRepo.save(any())).thenReturn(attendance);

        Attendance result = attendanceService.checkIn(1L);

        assertNotNull(result);
        assertEquals(Attendance.AttendanceType.WFO, result.getAttendanceType());
        verify(attendanceRepo).save(any());
    }

    @Test
    void checkIn_alreadyCheckedIn_throwsException() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepo.findByEmployeeAndWorkDate(employee, LocalDate.now()))
                .thenReturn(Optional.of(attendance));

        assertThrows(RuntimeException.class, () -> attendanceService.checkIn(1L));
        verify(attendanceRepo, never()).save(any());
    }

    @Test
    void checkIn_employeeNotFound_throwsException() {
        when(employeeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> attendanceService.checkIn(99L));
    }

    @Test
    void checkOut_success() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepo.findByEmployeeAndWorkDate(employee, LocalDate.now()))
                .thenReturn(Optional.of(attendance));
        when(attendanceRepo.save(any())).thenReturn(attendance);

        Attendance result = attendanceService.checkOut(1L);

        assertNotNull(result);
        verify(attendanceRepo).save(any());
    }

    @Test
    void checkOut_noCheckIn_throwsException() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepo.findByEmployeeAndWorkDate(employee, LocalDate.now()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> attendanceService.checkOut(1L));
    }

    @Test
    void getAll_returnsResponseList() {
        when(attendanceRepo.findAll()).thenReturn(List.of(attendance));

        List<AttendanceResponse> result = attendanceService.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());
        assertEquals("Engineering", result.get(0).getDepartmentName());
    }

    @Test
    void getSummary_returnsCorrectCounts() {
        LocalDate from = LocalDate.of(2025, 4, 1);
        LocalDate to = LocalDate.of(2025, 4, 30);

        when(employeeRepo.count()).thenReturn(10L);
        when(attendanceRepo.countByStatusAndWorkDateBetween(Attendance.Status.PRESENT, from, to)).thenReturn(8L);
        when(attendanceRepo.countByStatusAndWorkDateBetween(Attendance.Status.ABSENT, from, to)).thenReturn(1L);
        when(attendanceRepo.countByStatusAndWorkDateBetween(Attendance.Status.WFH, from, to)).thenReturn(1L);
        when(attendanceRepo.countByStatusAndWorkDateBetween(Attendance.Status.ON_LEAVE, from, to)).thenReturn(0L);
        when(attendanceRepo.countByAttendanceTypeAndWorkDateBetween(Attendance.AttendanceType.FIELD, from, to)).thenReturn(0L);

        AttendanceSummaryResponse result = attendanceService.getSummary(from, to);

        assertEquals(10L, result.getTotalEmployees());
        assertEquals(8L, result.getPresent());
        assertEquals(1L, result.getAbsent());
    }
}