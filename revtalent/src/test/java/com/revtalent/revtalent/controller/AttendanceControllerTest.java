package com.revtalent.revtalent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revtalent.revtalent.dto.AttendanceDTO;
import com.revtalent.revtalent.dto.AttendanceResponseDTO;
import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.dto.attendance.AttendanceSummaryResponse;
import com.revtalent.revtalent.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendanceController.class)
public class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttendanceService attendanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetByEmployee() throws Exception {

        AttendanceResponseDTO responseDTO = new AttendanceResponseDTO();

        Mockito.when(attendanceService.getByEmployee(1L))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/attendance/employee/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetByRange() throws Exception {

        AttendanceResponseDTO responseDTO = new AttendanceResponseDTO();

        Mockito.when(attendanceService.getByEmployeeAndDateRange(
                        eq(1L),
                        eq(LocalDate.parse("2025-05-01")),
                        eq(LocalDate.parse("2025-05-31"))
                ))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/attendance/employee/1/range")
                        .param("from", "2025-05-01")
                        .param("to", "2025-05-31"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCheckIn() throws Exception {

        AttendanceDTO dto = new AttendanceDTO();
        AttendanceResponseDTO responseDTO = new AttendanceResponseDTO();

        Mockito.when(attendanceService.checkIn(eq(1L), any(AttendanceDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/attendance/employee/1/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCheckOut() throws Exception {

        AttendanceResponseDTO responseDTO = new AttendanceResponseDTO();

        Mockito.when(attendanceService.checkOut(1L))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/attendance/employee/1/checkout"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testSaveAttendance() throws Exception {

        AttendanceDTO dto = new AttendanceDTO();
        AttendanceResponseDTO responseDTO = new AttendanceResponseDTO();

        Mockito.when(attendanceService.save(eq(1L), any(AttendanceDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/attendance/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testRegularizeAttendance() throws Exception {

        AttendanceDTO dto = new AttendanceDTO();
        AttendanceResponseDTO responseDTO = new AttendanceResponseDTO();

        Mockito.when(attendanceService.regularize(eq(1L), any(AttendanceDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/attendance/1/regularize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetPresentCount() throws Exception {

        Mockito.when(attendanceService.getPresentCount(
                        eq(1L),
                        eq(LocalDate.parse("2025-05-01")),
                        eq(LocalDate.parse("2025-05-31"))
                ))
                .thenReturn(20);

        mockMvc.perform(get("/api/attendance/employee/1/present-count")
                        .param("from", "2025-05-01")
                        .param("to", "2025-05-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    @WithMockUser(roles = "HR")
    void testGetAllAttendance() throws Exception {

        AttendanceResponse response = new AttendanceResponse();

        Mockito.when(attendanceService.getAll())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetSummary() throws Exception {

        Mockito.when(attendanceService.getAttendanceSummary())
                .thenReturn(List.of(Map.of("present", 10)));

        mockMvc.perform(get("/api/attendance/summary"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetHrSummary() throws Exception {

        AttendanceSummaryResponse response = new AttendanceSummaryResponse();

        Mockito.when(attendanceService.getSummary(
                        eq(LocalDate.parse("2025-05-01")),
                        eq(LocalDate.parse("2025-05-31"))
                ))
                .thenReturn(response);

        mockMvc.perform(get("/api/attendance/hr/summary")
                        .param("from", "2025-05-01")
                        .param("to", "2025-05-31"))
                .andExpect(status().isOk());
    }
}