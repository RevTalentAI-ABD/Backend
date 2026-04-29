package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeaveService Unit Tests")
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRepository;

    @InjectMocks
    private LeaveService leaveService;

    private LeaveRequest pendingLeave;
    private LeaveRequest approvedLeave;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L).username("emp_user").build();
        Employee employee = Employee.builder()
                .id(1L).user(user).status(Employee.Status.ACTIVE).build();

        pendingLeave = LeaveRequest.builder()
                .id(1L)
                .employee(employee)
                .leaveType(LeaveRequest.LeaveType.SICK)
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 5, 3))
                .totalDays(BigDecimal.valueOf(3))
                .status(LeaveRequest.Status.APPLIED)
                .reason("Fever")
                .appliedAt(LocalDateTime.now().minusDays(1))
                .build();

        approvedLeave = LeaveRequest.builder()
                .id(2L)
                .employee(employee)
                .leaveType(LeaveRequest.LeaveType.ANNUAL)
                .startDate(LocalDate.of(2025, 6, 1))
                .endDate(LocalDate.of(2025, 6, 5))
                .totalDays(BigDecimal.valueOf(5))
                .status(LeaveRequest.Status.APPROVED)
                .reason("Vacation")
                .appliedAt(LocalDateTime.now().minusDays(10))
                .build();
    }

    @Nested
    @DisplayName("getAllLeaves()")
    class GetAllLeaves {

        @Test
        @DisplayName("returns all leaves mapped to DTOs")
        void returnsAllLeaveMapped() {
            when(leaveRepository.findAll()).thenReturn(List.of(pendingLeave, approvedLeave));

            List<LeaveRequestDTO> result = leaveService.getAllLeaves();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(LeaveRequestDTO::getStatus)
                    .containsExactlyInAnyOrder("APPLIED", "APPROVED");
        }

        @Test
        @DisplayName("maps employee name from user username")
        void mapsEmployeeName() {
            when(leaveRepository.findAll()).thenReturn(List.of(pendingLeave));

            LeaveRequestDTO dto = leaveService.getAllLeaves().get(0);

            assertThat(dto.getEmployeeName()).isEqualTo("emp_user");
        }

        @Test
        @DisplayName("maps 'N/A' when employee or user is null")
        void mapsNAForNullEmployee() {
            LeaveRequest noEmpLeave = LeaveRequest.builder()
                    .id(3L).employee(null)
                    .leaveType(LeaveRequest.LeaveType.CASUAL)
                    .startDate(LocalDate.now()).endDate(LocalDate.now())
                    .totalDays(BigDecimal.ONE)
                    .status(LeaveRequest.Status.APPLIED)
                    .appliedAt(LocalDateTime.now())
                    .build();
            when(leaveRepository.findAll()).thenReturn(List.of(noEmpLeave));

            LeaveRequestDTO dto = leaveService.getAllLeaves().get(0);

            assertThat(dto.getEmployeeName()).isEqualTo("N/A");
        }

        @Test
        @DisplayName("returns empty list when no leaves exist")
        void returnsEmptyList() {
            when(leaveRepository.findAll()).thenReturn(List.of());

            assertThat(leaveService.getAllLeaves()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPendingLeaves()")
    class GetPendingLeaves {

        @Test
        @DisplayName("returns only APPLIED leaves from repository")
        void returnsOnlyPending() {
            when(leaveRepository.findByStatus(LeaveRequest.Status.APPLIED))
                    .thenReturn(List.of(pendingLeave));

            List<LeaveRequestDTO> result = leaveService.getPendingLeaves();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("APPLIED");
            verify(leaveRepository).findByStatus(LeaveRequest.Status.APPLIED);
        }

        @Test
        @DisplayName("returns empty list when no pending leaves")
        void returnsEmptyWhenNoPending() {
            when(leaveRepository.findByStatus(LeaveRequest.Status.APPLIED))
                    .thenReturn(List.of());

            assertThat(leaveService.getPendingLeaves()).isEmpty();
        }
    }

    @Nested
    @DisplayName("approveLeave()")
    class ApproveLeave {

        @Test
        @DisplayName("sets status to APPROVED and saves")
        void approvesAndSaves() {
            when(leaveRepository.findById(1L)).thenReturn(Optional.of(pendingLeave));

            leaveService.approveLeave(1L);

            ArgumentCaptor<LeaveRequest> captor = ArgumentCaptor.forClass(LeaveRequest.class);
            verify(leaveRepository).save(captor.capture());
            LeaveRequest saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(LeaveRequest.Status.APPROVED);
            assertThat(saved.getActionedAt()).isNotNull();
        }

        @Test
        @DisplayName("throws RuntimeException when leave id not found")
        void throwsWhenNotFound() {
            when(leaveRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> leaveService.approveLeave(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Leave not found with id: 99");

            verify(leaveRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws RuntimeException when leave is already APPROVED (not APPLIED)")
        void throwsWhenAlreadyApproved() {
            when(leaveRepository.findById(2L)).thenReturn(Optional.of(approvedLeave));

            assertThatThrownBy(() -> leaveService.approveLeave(2L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Only pending leaves can be approved");

            verify(leaveRepository, never()).save(any());
        }

        @Test
        @DisplayName("sets actionedAt timestamp close to now")
        void setsActionedAt() {
            when(leaveRepository.findById(1L)).thenReturn(Optional.of(pendingLeave));
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            leaveService.approveLeave(1L);

            ArgumentCaptor<LeaveRequest> captor = ArgumentCaptor.forClass(LeaveRequest.class);
            verify(leaveRepository).save(captor.capture());
            assertThat(captor.getValue().getActionedAt()).isAfter(before);
        }
    }

    @Nested
    @DisplayName("rejectLeave()")
    class RejectLeave {

        @Test
        @DisplayName("sets status to REJECTED with rejection reason and saves")
        void rejectsWithReason() {
            when(leaveRepository.findById(1L)).thenReturn(Optional.of(pendingLeave));

            leaveService.rejectLeave(1L, "Insufficient leave balance");

            ArgumentCaptor<LeaveRequest> captor = ArgumentCaptor.forClass(LeaveRequest.class);
            verify(leaveRepository).save(captor.capture());
            LeaveRequest saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(LeaveRequest.Status.REJECTED);
            assertThat(saved.getRejectionReason()).isEqualTo("Insufficient leave balance");
            assertThat(saved.getActionedAt()).isNotNull();
        }

        @Test
        @DisplayName("throws RuntimeException when leave id not found")
        void throwsWhenNotFound() {
            when(leaveRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> leaveService.rejectLeave(99L, "reason"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Leave not found with id: 99");
        }

        @Test
        @DisplayName("throws RuntimeException when leave is already APPROVED")
        void throwsWhenNotPending() {
            when(leaveRepository.findById(2L)).thenReturn(Optional.of(approvedLeave));

            assertThatThrownBy(() -> leaveService.rejectLeave(2L, "too late"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Only pending leaves can be rejected");

            verify(leaveRepository, never()).save(any());
        }

        @Test
        @DisplayName("persists null rejection reason when none provided")
        void persistsNullReason() {
            when(leaveRepository.findById(1L)).thenReturn(Optional.of(pendingLeave));

            leaveService.rejectLeave(1L, null);

            ArgumentCaptor<LeaveRequest> captor = ArgumentCaptor.forClass(LeaveRequest.class);
            verify(leaveRepository).save(captor.capture());
            assertThat(captor.getValue().getRejectionReason()).isNull();
        }
    }
}
