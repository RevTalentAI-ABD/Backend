package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.service.LeaveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaveController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("LeaveController – MockMvc Tests")
class LeaveControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private LeaveService leaveService;

    // ─── shared stub ────────────────────────────────────────────────────────
    private LeaveRequestDTO pendingDTO() {
        return LeaveRequestDTO.builder()
                .id(1L)
                .employeeName("emp_user")
                .leaveType("SICK")
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 5, 3))
                .totalDays(BigDecimal.valueOf(3))
                .status("APPLIED")
                .reason("Fever")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  GET /api/manager/leaves
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("GET /api/manager/leaves")
    class GetAllLeaves {

        @Test
        @DisplayName("returns 200 with list of all leaves")
        void returns200WithLeaveList() throws Exception {
            when(leaveService.getAllLeaves()).thenReturn(List.of(pendingDTO()));

            mockMvc.perform(get("/api/manager/leaves").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].employeeName").value("emp_user"))
                    .andExpect(jsonPath("$[0].leaveType").value("SICK"))
                    .andExpect(jsonPath("$[0].status").value("APPLIED"));
        }

        @Test
        @DisplayName("returns 200 with empty list when no leaves")
        void returns200EmptyList() throws Exception {
            when(leaveService.getAllLeaves()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/leaves").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  GET /api/manager/leaves/pending
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("GET /api/manager/leaves/pending")
    class GetPendingLeaves {

        @Test
        @DisplayName("returns 200 with pending leaves only")
        void returns200WithPending() throws Exception {
            when(leaveService.getPendingLeaves()).thenReturn(List.of(pendingDTO()));

            mockMvc.perform(get("/api/manager/leaves/pending").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value("APPLIED"));
        }

        @Test
        @DisplayName("delegates to leaveService.getPendingLeaves()")
        void delegatesToService() throws Exception {
            when(leaveService.getPendingLeaves()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/leaves/pending"));

            verify(leaveService, times(1)).getPendingLeaves();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PUT /api/manager/leaves/{id}/approve
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("PUT /api/manager/leaves/{id}/approve")
    class ApproveLeave {

        @Test
        @DisplayName("returns 200 'Approved' on successful approval")
        void returns200Approved() throws Exception {
            doNothing().when(leaveService).approveLeave(1L);

            mockMvc.perform(put("/api/manager/leaves/1/approve"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Approved"));
        }

        @Test
        @DisplayName("calls leaveService.approveLeave(id) with correct id")
        void delegatesWithCorrectId() throws Exception {
            doNothing().when(leaveService).approveLeave(42L);

            mockMvc.perform(put("/api/manager/leaves/42/approve"));

            verify(leaveService).approveLeave(42L);
        }

        @Test
        @DisplayName("returns 404 when leave cannot be approved")
        void returns404WhenCannotApprove() throws Exception {
            doThrow(new RuntimeException("Only pending leaves can be approved"))
                    .when(leaveService).approveLeave(2L);

            mockMvc.perform(put("/api/manager/leaves/2/approve"))
                    .andExpect(status().isNotFound());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PUT /api/manager/leaves/{id}/reject
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("PUT /api/manager/leaves/{id}/reject")
    class RejectLeave {

        @Test
        @DisplayName("returns 200 'Rejected' on successful rejection")
        void returns200Rejected() throws Exception {
            doNothing().when(leaveService).rejectLeave(1L, "No balance");

            mockMvc.perform(put("/api/manager/leaves/1/reject")
                            .param("reason", "No balance"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Rejected"));
        }

        @Test
        @DisplayName("passes reason param to leaveService.rejectLeave()")
        void passesReasonParam() throws Exception {
            doNothing().when(leaveService).rejectLeave(anyLong(), anyString());

            mockMvc.perform(put("/api/manager/leaves/1/reject")
                    .param("reason", "Insufficient balance"));

            verify(leaveService).rejectLeave(1L, "Insufficient balance");
        }

        @Test
        @DisplayName("passes null reason when param is absent")
        void passesNullReasonWhenAbsent() throws Exception {
            doNothing().when(leaveService).rejectLeave(anyLong(), isNull());

            mockMvc.perform(put("/api/manager/leaves/1/reject"))
                    .andExpect(status().isOk());

            verify(leaveService).rejectLeave(1L, null);
        }

        @Test
        @DisplayName("returns 404 when leave cannot be rejected")
        void returns404WhenCannotReject() throws Exception {
            doThrow(new RuntimeException("Only pending leaves can be rejected"))
                    .when(leaveService).rejectLeave(2L, null);

            mockMvc.perform(put("/api/manager/leaves/2/reject"))
                    .andExpect(status().isNotFound());
        }
    }
}