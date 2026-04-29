package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ManagerService Unit Tests")
class ManagerServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private AttendanceRepository attendanceRepository;
    @Mock private LeaveRequestRepository leaveRepository;

    @InjectMocks
    private ManagerService managerService;

    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
    }

    private Attendance makeAttendance(Attendance.Status status, LocalDate date, LocalDateTime checkIn) {
        User user = User.builder().username("emp" + Math.random()).build();
        Employee emp = Employee.builder().id(1L).user(user).build();
        return Attendance.builder()
                .id(1L).employee(emp).workDate(date)
                .status(status).attendanceType(Attendance.AttendanceType.WFO)
                .checkIn(checkIn).build();
    }

    private LeaveRequest makeLeave(LeaveRequest.Status status, LocalDateTime appliedAt,
                                   LocalDateTime actionedAt) {
        User user = User.builder().username("leaveEmp").build();
        Employee emp = Employee.builder().id(2L).user(user).build();
        return LeaveRequest.builder()
                .id(1L).employee(emp)
                .leaveType(LeaveRequest.LeaveType.SICK)
                .status(status)
                .appliedAt(appliedAt)
                .actionedAt(actionedAt)
                .build();
    }
    @Nested
    @DisplayName("getDashboard()")
    class GetDashboard {

        @Test
        @DisplayName("returns correct dashboard counts from repos")
        void returnsDashboardCounts() {
            when(employeeRepository.count()).thenReturn(20L);
            when(attendanceRepository.countByStatusAndWorkDate(Attendance.Status.PRESENT, today)).thenReturn(10);
            when(attendanceRepository.countByStatusAndWorkDate(Attendance.Status.WFH, today)).thenReturn(3);
            when(attendanceRepository.countByStatusAndWorkDate(Attendance.Status.ABSENT, today)).thenReturn(2);
            when(attendanceRepository.countByStatusAndWorkDate(Attendance.Status.ON_LEAVE, today)).thenReturn(5);
            when(leaveRepository.countByStatus(LeaveRequest.Status.APPLIED)).thenReturn(4);

            Map<String, Object> dashboard = managerService.getDashboard();

            assertThat(dashboard.get("teamSize")).isEqualTo(20);
            assertThat(dashboard.get("present")).isEqualTo(10);
            assertThat(dashboard.get("wfh")).isEqualTo(3);
            assertThat(dashboard.get("absent")).isEqualTo(2);
            assertThat(dashboard.get("onLeave")).isEqualTo(5);
            assertThat(dashboard.get("pendingLeaves")).isEqualTo(4);
        }

        @Test
        @DisplayName("returns zeroes when all counts are zero")
        void returnsZeroesWhenEmpty() {
            when(employeeRepository.count()).thenReturn(0L);
            when(attendanceRepository.countByStatusAndWorkDate(any(), eq(today))).thenReturn(0);
            when(leaveRepository.countByStatus(LeaveRequest.Status.APPLIED)).thenReturn(0);

            Map<String, Object> dashboard = managerService.getDashboard();

            assertThat(dashboard.get("teamSize")).isEqualTo(0);
            assertThat(dashboard.get("present")).isEqualTo(0);
        }

        @Test
        @DisplayName("queries attendance for today's date")
        void queriesForToday() {
            when(employeeRepository.count()).thenReturn(5L);
            when(attendanceRepository.countByStatusAndWorkDate(any(), any())).thenReturn(0);
            when(leaveRepository.countByStatus(any())).thenReturn(0);

            managerService.getDashboard();

            verify(attendanceRepository).countByStatusAndWorkDate(Attendance.Status.PRESENT, today);
            verify(attendanceRepository).countByStatusAndWorkDate(Attendance.Status.ABSENT, today);
            verify(attendanceRepository).countByStatusAndWorkDate(Attendance.Status.WFH, today);
            verify(attendanceRepository).countByStatusAndWorkDate(Attendance.Status.ON_LEAVE, today);
        }
    }

    @Nested
    @DisplayName("getProductivity()")
    class GetProductivity {

        @Test
        @DisplayName("returns grouped monthly productivity with correct score")
        void returnsMonthlyProductivity() {
            LocalDate may1 = LocalDate.of(2025, 5, 1);
            Attendance present = makeAttendance(Attendance.Status.PRESENT, may1, null);
            Attendance wfh     = makeAttendance(Attendance.Status.WFH, may1.plusDays(1), null);
            Attendance absent  = makeAttendance(Attendance.Status.ABSENT, may1.plusDays(2), null);

            when(attendanceRepository.findAll()).thenReturn(List.of(present, wfh, absent));

            List<Map<String, Object>> result = managerService.getProductivity();

            assertThat(result).hasSize(1); // all in May
            Map<String, Object> may = result.get(0);
            assertThat(may.get("month")).isEqualTo("May");
            assertThat((long) (Long) may.get("present")).isEqualTo(2L); // PRESENT + WFH
            assertThat((long) (Long) may.get("total")).isEqualTo(3L);
            assertThat((int) (Integer) may.get("score")).isEqualTo(66);  // (2/3)*100
        }

        @Test
        @DisplayName("returns score 0 when month has no attendance records")
        void returnsZeroScoreForEmpty() {
            when(attendanceRepository.findAll()).thenReturn(List.of());

            List<Map<String, Object>> result = managerService.getProductivity();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("groups attendance by month correctly across multiple months")
        void groupsByMonthCorrectly() {
            Attendance june   = makeAttendance(Attendance.Status.PRESENT, LocalDate.of(2025, 6, 1), null);
            Attendance july   = makeAttendance(Attendance.Status.ABSENT, LocalDate.of(2025, 7, 1), null);

            when(attendanceRepository.findAll()).thenReturn(List.of(june, july));

            List<Map<String, Object>> result = managerService.getProductivity();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(m -> m.get("month"))
                    .containsExactlyInAnyOrder("Jun", "Jul");
        }
    }

    @Nested
    @DisplayName("getTeamSummary()")
    class GetTeamSummary {

        @Test
        @DisplayName("returns all team summary fields with correct values")
        void returnsTeamSummary() {
            when(employeeRepository.count()).thenReturn(30L);
            when(employeeRepository.countByStatus(Employee.Status.ACTIVE)).thenReturn(25L);
            when(employeeRepository.countByStatus(Employee.Status.ON_LEAVE)).thenReturn(3L);
            when(employeeRepository.countByStatus(Employee.Status.INACTIVE)).thenReturn(2L);
            when(leaveRepository.countByStatus(LeaveRequest.Status.APPLIED)).thenReturn(5);
            when(leaveRepository.countByStatus(LeaveRequest.Status.APPROVED)).thenReturn(8);
            when(attendanceRepository.countByStatusAndWorkDate(Attendance.Status.PRESENT, today)).thenReturn(15);
            when(attendanceRepository.countByStatusAndWorkDate(Attendance.Status.WFH, today)).thenReturn(5);
            when(attendanceRepository.countByStatusAndWorkDate(Attendance.Status.ABSENT, today)).thenReturn(2);

            Map<String, Object> summary = managerService.getTeamSummary();

            assertThat(summary.get("totalEmployees")).isEqualTo(30L);
            assertThat(summary.get("activeEmployees")).isEqualTo(25L);
            assertThat(summary.get("onLeave")).isEqualTo(3L);
            assertThat(summary.get("inactive")).isEqualTo(2L);
            assertThat(summary.get("pendingLeaves")).isEqualTo(5L);
            assertThat(summary.get("approvedLeaves")).isEqualTo(8L);
            assertThat(summary.get("presentToday")).isEqualTo(15L);
            assertThat(summary.get("wfhToday")).isEqualTo(5L);
            assertThat(summary.get("absentToday")).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("getActivity()")
    class GetActivity {

        @Test
        @DisplayName("includes recent leave applications from last 7 days")
        void includesRecentLeaveApplications() {
            LeaveRequest recent = makeLeave(
                    LeaveRequest.Status.APPLIED,
                    LocalDateTime.now().minusDays(1),   // appliedAt within 7 days
                    null
            );
            when(leaveRepository.findAll()).thenReturn(List.of(recent));
            when(attendanceRepository.findByWorkDate(today)).thenReturn(List.of());

            List<Map<String, Object>> activities = managerService.getActivity();

            assertThat(activities).anyMatch(a -> "LEAVE".equals(a.get("type")));
        }

        @Test
        @DisplayName("excludes leave applications older than 7 days")
        void excludesOldLeaveApplications() {
            LeaveRequest old = makeLeave(
                    LeaveRequest.Status.APPLIED,
                    LocalDateTime.now().minusDays(10),  // older than 7 days
                    null
            );
            when(leaveRepository.findAll()).thenReturn(List.of(old));
            when(attendanceRepository.findByWorkDate(today)).thenReturn(List.of());

            List<Map<String, Object>> activities = managerService.getActivity();

            assertThat(activities).noneMatch(a -> "LEAVE".equals(a.get("type")));
        }

        @Test
        @DisplayName("includes today's check-in activities")
        void includesTodayCheckIns() {
            Attendance checkedIn = makeAttendance(
                    Attendance.Status.PRESENT, today, LocalDateTime.now().minusHours(1)
            );
            when(leaveRepository.findAll()).thenReturn(List.of());
            when(attendanceRepository.findByWorkDate(today)).thenReturn(List.of(checkedIn));

            List<Map<String, Object>> activities = managerService.getActivity();

            assertThat(activities).anyMatch(a -> "ATTENDANCE".equals(a.get("type")));
        }

        @Test
        @DisplayName("does not include attendance records with null checkIn")
        void excludesNullCheckIn() {
            Attendance noCheckIn = makeAttendance(Attendance.Status.ABSENT, today, null);
            when(leaveRepository.findAll()).thenReturn(List.of());
            when(attendanceRepository.findByWorkDate(today)).thenReturn(List.of(noCheckIn));

            List<Map<String, Object>> activities = managerService.getActivity();

            assertThat(activities).noneMatch(a -> "ATTENDANCE".equals(a.get("type")));
        }

        @Test
        @DisplayName("returns empty list when no leaves and no attendance today")
        void returnsEmptyWhenNothing() {
            when(leaveRepository.findAll()).thenReturn(List.of());
            when(attendanceRepository.findByWorkDate(today)).thenReturn(List.of());

            List<Map<String, Object>> activities = managerService.getActivity();

            assertThat(activities).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAttendanceReport()")
    class GetAttendanceReport {

        @Test
        @DisplayName("report contains today, monthSummary, perEmployee, and reportMonth keys")
        void reportContainsRequiredKeys() {
            when(attendanceRepository.findAll()).thenReturn(List.of());

            Map<String, Object> report = managerService.getAttendanceReport();

            assertThat(report).containsKeys("reportMonth", "today", "monthSummary", "perEmployee");
        }

        @Test
        @DisplayName("attendanceRate is 100% when all records are PRESENT or WFH")
        void attendanceRate100WhenAllPresent() {
            Attendance p1 = makeAttendance(Attendance.Status.PRESENT, today, null);
            Attendance p2 = makeAttendance(Attendance.Status.WFH, today, null);
            when(attendanceRepository.findAll()).thenReturn(List.of(p1, p2));

            Map<String, Object> report = managerService.getAttendanceReport();

            @SuppressWarnings("unchecked")
            Map<String, Object> monthSummary = (Map<String, Object>) report.get("monthSummary");
            assertThat(monthSummary.get("attendanceRate")).isEqualTo("100%");
        }

        @Test
        @DisplayName("attendanceRate is 0% when all records are ABSENT")
        void attendanceRate0WhenAllAbsent() {
            Attendance absent = makeAttendance(Attendance.Status.ABSENT, today, null);
            when(attendanceRepository.findAll()).thenReturn(List.of(absent));

            Map<String, Object> report = managerService.getAttendanceReport();

            @SuppressWarnings("unchecked")
            Map<String, Object> monthSummary = (Map<String, Object>) report.get("monthSummary");
            assertThat(monthSummary.get("attendanceRate")).isEqualTo("0%");
        }

        @Test
        @DisplayName("perEmployee list has one entry per unique employee in current month")
        void perEmployeeGroupsCorrectly() {
            User user1 = User.builder().username("alice").build();
            User user2 = User.builder().username("bob").build();
            Employee emp1 = Employee.builder().id(1L).user(user1).build();
            Employee emp2 = Employee.builder().id(2L).user(user2).build();

            Attendance a1 = Attendance.builder()
                    .id(1L).employee(emp1).workDate(today)
                    .status(Attendance.Status.PRESENT).attendanceType(Attendance.AttendanceType.WFO).build();
            Attendance a2 = Attendance.builder()
                    .id(2L).employee(emp2).workDate(today)
                    .status(Attendance.Status.ABSENT).attendanceType(Attendance.AttendanceType.WFO).build();

            when(attendanceRepository.findAll()).thenReturn(List.of(a1, a2));

            Map<String, Object> report = managerService.getAttendanceReport();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> perEmp = (List<Map<String, Object>>) report.get("perEmployee");
            assertThat(perEmp).hasSize(2);
        }
    }
}