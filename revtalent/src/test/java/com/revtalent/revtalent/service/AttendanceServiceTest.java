package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
<<<<<<< HEAD
import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Department;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
=======
import com.revtalent.revtalent.dto.attendance.AttendanceSummaryResponse;
import com.revtalent.revtalent.model.*;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
>>>>>>> origin/HRModule
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
<<<<<<< HEAD
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService Unit Tests")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Attendance testAttendance;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("testuser").build();
        Department dept = Department.builder().id(1L).name("Engineering").build();
        Employee emp = Employee.builder().id(1L).user(user).department(dept).build();

        testAttendance = Attendance.builder()
                .id(1L)
                .employee(emp)
                .workDate(LocalDate.of(2025, 5, 1))
                .checkIn(LocalDateTime.of(2025, 5, 1, 9, 0))
                .checkOut(LocalDateTime.of(2025, 5, 1, 18, 0))
                .durationMin(540)
                .attendanceType(Attendance.AttendanceType.WFO)
                .status(Attendance.Status.PRESENT)
                .isRegularized(false)
                .notes("On time")
                .build();
    }

    @Nested
    @DisplayName("getAttendance()")
    class GetAttendance {

        @Test
        @DisplayName("maps all attendance fields to AttendanceResponse correctly")
        void mapsAllFields() {
            when(attendanceRepository.findAll()).thenReturn(List.of(testAttendance));

            List<AttendanceResponse> result = attendanceService.getAttendance();

            assertThat(result).hasSize(1);
            AttendanceResponse r = result.get(0);
            assertThat(r.getId()).isEqualTo(1L);
            assertThat(r.getEmployeeId()).isEqualTo(1L);
            assertThat(r.getEmployeeName()).isEqualTo("testuser");
            assertThat(r.getDepartment()).isEqualTo("Engineering");
            assertThat(r.getWorkDate()).isEqualTo(LocalDate.of(2025, 5, 1));
            assertThat(r.getCheckIn()).isEqualTo(LocalDateTime.of(2025, 5, 1, 9, 0));
            assertThat(r.getCheckOut()).isEqualTo(LocalDateTime.of(2025, 5, 1, 18, 0));
            assertThat(r.getDurationMin()).isEqualTo(540);
            assertThat(r.getAttendanceType()).isEqualTo("WFO");
            assertThat(r.getStatus()).isEqualTo("PRESENT");
            assertThat(r.isRegularized()).isFalse();
            assertThat(r.getNotes()).isEqualTo("On time");
        }

        @Test
        @DisplayName("maps null employeeId and 'N/A' name when employee is null")
        void mapsNullEmployee() {
            Attendance noEmp = Attendance.builder()
                    .id(2L).employee(null)
                    .workDate(LocalDate.now())
                    .attendanceType(Attendance.AttendanceType.WFO)
                    .status(Attendance.Status.ABSENT)
                    .build();
            when(attendanceRepository.findAll()).thenReturn(List.of(noEmp));

            AttendanceResponse r = attendanceService.getAttendance().get(0);

            assertThat(r.getEmployeeId()).isNull();
            assertThat(r.getEmployeeName()).isEqualTo("N/A");
            assertThat(r.getDepartment()).isEqualTo("N/A");
        }

        @Test
        @DisplayName("returns empty list when no records exist")
        void returnsEmpty() {
            when(attendanceRepository.findAll()).thenReturn(List.of());

            assertThat(attendanceService.getAttendance()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAttendanceSummary()")
    class GetAttendanceSummary {

        @Test
        @DisplayName("groups attendance records by week start (Monday)")
        void groupsByWeek() {
            // 2025-04-28 is Monday; 2025-04-30 is Wednesday (same week)
            LocalDate monday = LocalDate.of(2025, 4, 28);
            Attendance a1 = buildForDate(monday, Attendance.Status.PRESENT);
            Attendance a2 = buildForDate(monday.plusDays(2), Attendance.Status.ABSENT);

            when(attendanceRepository.findAll()).thenReturn(List.of(a1, a2));

            List<Map<String, Object>> summary = attendanceService.getAttendanceSummary();

            assertThat(summary).hasSize(1);          // both in same week
            assertThat(summary.get(0).get("present")).isEqualTo(1L);
            assertThat(summary.get(0).get("absent")).isEqualTo(1L);
        }

        @Test
        @DisplayName("separates records from different weeks")
        void separatesDifferentWeeks() {
            Attendance w1 = buildForDate(LocalDate.of(2025, 4, 28), Attendance.Status.PRESENT); // week 1
            Attendance w2 = buildForDate(LocalDate.of(2025, 5, 5), Attendance.Status.PRESENT);  // week 2

            when(attendanceRepository.findAll()).thenReturn(List.of(w1, w2));

            List<Map<String, Object>> summary = attendanceService.getAttendanceSummary();

            assertThat(summary).hasSize(2);
        }

        @Test
        @DisplayName("correctly counts WFH, ON_LEAVE records by week")
        void countsAllStatuses() {
            LocalDate monday = LocalDate.of(2025, 4, 28);
            Attendance wfh    = buildForDate(monday, Attendance.Status.WFH);
            Attendance onLeave = buildForDate(monday.plusDays(1), Attendance.Status.ON_LEAVE);

            when(attendanceRepository.findAll()).thenReturn(List.of(wfh, onLeave));

            Map<String, Object> week = attendanceService.getAttendanceSummary().get(0);

            assertThat(week.get("wfh")).isEqualTo(1L);
            assertThat(week.get("leave")).isEqualTo(1L);
            assertThat(week.get("present")).isEqualTo(0L);
            assertThat(week.get("absent")).isEqualTo(0L);
        }

        @Test
        @DisplayName("returns empty list when no attendance records")
        void returnsEmptyList() {
            when(attendanceRepository.findAll()).thenReturn(List.of());

            assertThat(attendanceService.getAttendanceSummary()).isEmpty();
        }

        private Attendance buildForDate(LocalDate date, Attendance.Status status) {
            return Attendance.builder()
                    .id((long) (Math.random() * 1000))
                    .employee(testAttendance.getEmployee())
                    .workDate(date)
                    .status(status)
                    .attendanceType(Attendance.AttendanceType.WFO)
                    .build();
        }
    }

    @Nested
    @DisplayName("exportAttendanceAsCsv()")
    class ExportCsv {

        @Test
        @DisplayName("CSV output starts with the correct header row")
        void csvHasHeaderRow() {
            when(attendanceRepository.findAll()).thenReturn(List.of());

            byte[] csv = attendanceService.exportAttendanceAsCsv();
            String output = new String(csv);

            assertThat(output).startsWith("ID,Employee ID,Employee Name,Department,Work Date");
        }

        @Test
        @DisplayName("CSV contains one data row per attendance record")
        void csvHasOneRowPerRecord() {
            when(attendanceRepository.findAll()).thenReturn(List.of(testAttendance));

            String csv = new String(attendanceService.exportAttendanceAsCsv());
            long dataLines = csv.lines().skip(1).filter(l -> !l.isBlank()).count();

            assertThat(dataLines).isEqualTo(1);
        }

        @Test
        @DisplayName("CSV data row contains employee name and status")
        void csvDataRowContainsCorrectFields() {
            when(attendanceRepository.findAll()).thenReturn(List.of(testAttendance));

            String csv = new String(attendanceService.exportAttendanceAsCsv());

            assertThat(csv).contains("testuser");
            assertThat(csv).contains("PRESENT");
            assertThat(csv).contains("2025-05-01");
        }

        @Test
        @DisplayName("CSV handles null checkIn and checkOut as empty string")
        void csvHandlesNullTimes() {
            testAttendance.setCheckIn(null);
            testAttendance.setCheckOut(null);
            when(attendanceRepository.findAll()).thenReturn(List.of(testAttendance));

            String csv = new String(attendanceService.exportAttendanceAsCsv());

            assertThat(csv).doesNotContain("null");
        }

        @Test
        @DisplayName("returns only header when no records exist")
        void csvOnlyHeaderWhenEmpty() {
            when(attendanceRepository.findAll()).thenReturn(List.of());

            String csv = new String(attendanceService.exportAttendanceAsCsv());
            long lines = csv.lines().filter(l -> !l.isBlank()).count();

            assertThat(lines).isEqualTo(1);
        }
=======
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
>>>>>>> origin/HRModule
    }
}