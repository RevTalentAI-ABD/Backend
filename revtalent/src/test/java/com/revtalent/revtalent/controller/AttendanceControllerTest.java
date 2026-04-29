package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.dto.attendance.AttendanceSummaryResponse;
import com.revtalent.revtalent.service.AttendanceService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private AttendanceService service;

    // ✅ TEST 1
    @Test
    @WithMockUser
    void getAll_returns200() throws Exception {

        when(service.getAll()).thenReturn(List.of(new AttendanceResponse()));

        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ✅ TEST 2
    @Test
    @WithMockUser
    void getSummary_returns200() throws Exception {

        AttendanceSummaryResponse summary =
                new AttendanceSummaryResponse(10L, 8L, 1L, 1L, 0L, 0L);

        when(service.getSummary(any(), any())).thenReturn(summary);

        mockMvc.perform(get("/api/attendance/summary")
                        .param("from", "2025-04-01")
                        .param("to", "2025-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(10))
                .andExpect(jsonPath("$.present").value(8));
    }

    // ✅ TEST 3 (important)
    @Test
    void getAll_noAuth_returns401() throws Exception {

        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isUnauthorized());
    }
}