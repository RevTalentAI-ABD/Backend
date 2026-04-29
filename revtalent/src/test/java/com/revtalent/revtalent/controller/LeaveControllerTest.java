package com.revtalent.revtalent.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.exception.GlobalExceptionHandler;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.service.LeaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaveController.class)
@Import(GlobalExceptionHandler.class)
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeaveService leaveService;



    @Test
    @WithMockUser
    void getBalance_whenExists_returns200() throws Exception {
        when(leaveService.getLeaveBalance(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/leaves/balance/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getBalance_whenEmployeeNotFound_returns404() throws Exception {
        when(leaveService.getLeaveBalance(99L))
                .thenThrow(new RuntimeException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/leaves/balance/99"))
                .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser
    void getHistory_whenExists_returns200() throws Exception {
        LeaveRequest leave = new LeaveRequest();
        leave.setId(1L);
        leave.setStatus(LeaveRequest.Status.APPLIED);
        leave.setTotalDays(BigDecimal.valueOf(2));

        when(leaveService.getLeaveHistory(1L)).thenReturn(List.of(leave));

        mockMvc.perform(get("/api/leaves/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPLIED"));
    }



    @Test
    @WithMockUser
    void applyLeave_success_returns200() throws Exception {
        LeaveApplyDTO dto = new LeaveApplyDTO();
        dto.setEmployeeId(1L);
        dto.setLeaveType("ANNUAL");
        dto.setFromDate(LocalDate.now().plusDays(1));
        dto.setToDate(LocalDate.now().plusDays(3));
        dto.setReason("Family vacation");

        LeaveRequest leave = new LeaveRequest();
        leave.setId(1L);
        leave.setStatus(LeaveRequest.Status.APPLIED);
        leave.setTotalDays(BigDecimal.valueOf(3));

        when(leaveService.applyLeave(any(LeaveApplyDTO.class))).thenReturn(leave);

        mockMvc.perform(post("/api/leaves/apply")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    @WithMockUser
    void applyLeave_whenOverlapping_returns409() throws Exception {
        LeaveApplyDTO dto = new LeaveApplyDTO();
        dto.setEmployeeId(1L);
        dto.setLeaveType("ANNUAL");
        dto.setFromDate(LocalDate.now().plusDays(1));
        dto.setToDate(LocalDate.now().plusDays(3));
        dto.setReason("Test");

        when(leaveService.applyLeave(any(LeaveApplyDTO.class)))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT,
                        "Leave already applied for overlapping dates"));

        mockMvc.perform(post("/api/leaves/apply")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    // ── DELETE /api/leaves/{leaveId}/cancel ───────────────────────────────────

    @Test
    @WithMockUser
    void cancelLeave_success_returns200() throws Exception {
        doNothing().when(leaveService).cancelLeave(1L);

        mockMvc.perform(delete("/api/leaves/1/cancel")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void cancelLeave_whenNotFound_returns404() throws Exception {
        doThrow(new RuntimeException("Leave not found with id: 99"))
                .when(leaveService).cancelLeave(99L);

        mockMvc.perform(delete("/api/leaves/99/cancel")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}