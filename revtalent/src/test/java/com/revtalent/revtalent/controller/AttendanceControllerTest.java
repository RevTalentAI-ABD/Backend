package com.revtalent.revtalent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revtalent.revtalent.dto.AttendanceDTO;
import com.revtalent.revtalent.dto.AttendanceResponseDTO;
import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.dto.attendance.AttendanceSummaryResponse;
import com.revtalent.revtalent.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceController Tests")
class AttendanceControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    // ── Shared test data ──────────────────────────────────────────────────────

    private final Long EMP_ID = 1L;
    private final Long ATTENDANCE_ID = 10L;
    private final LocalDate FROM = LocalDate.of(2025, 1, 1);
    private final LocalDate TO   = LocalDate.of(2025, 1, 31);

    private AttendanceDTO sampleDTO;
    private AttendanceResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(attendanceController).build();

        // Build minimal DTO fixtures — adjust fields to match your actual DTOs
        sampleDTO = new AttendanceDTO();
        // e.g. sampleDTO.setDate(LocalDate.now());

        sampleResponseDTO = new AttendanceResponseDTO();
        // e.g. sampleResponseDTO.setId(ATTENDANCE_ID);
    }

    // ── GET /api/attendance/employee/{empId} ──────────────────────────────────


    @Test
    @DisplayName("GET /employee/{empId} → 200 with empty list when no records")
    void getByEmployee_emptyList() throws Exception {
        when(attendanceService.getByEmployee(EMP_ID))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/attendance/employee/{empId}", EMP_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/attendance/employee/{empId}/range ────────────────────────────

//    @Test
//    @DisplayName("GET /employee/{empId}/range → 200 with date-filtered list")
//    void getByRange_returnsOk() throws Exception {
//        when(attendanceService.getByEmployeeAndDateRange(EMP_ID, FROM, TO))
//                .thenReturn(List.of(sampleResponseDTO));
//
//        mockMvc.perform(get("/api/attendance/employee/{empId}/range", EMP_ID)
//                        .param("from", FROM.toString())
//                        .param("to",   TO.toString()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//
//        verify(attendanceService).getByEmployeeAndDateRange(EMP_ID, FROM, TO);
//    }

//    @Test
//    @DisplayName("GET /employee/{empId}/range → 400 when params missing")
//    void getByRange_missingParams_returnsBadRequest() throws Exception {
//        mockMvc.perform(get("/api/attendance/employee/{empId}/range", EMP_ID))
//                .andExpect(status().isBadRequest());
//    }

    // ── POST /api/attendance/employee/{empId}/checkin ─────────────────────────

    @Test
    @DisplayName("POST /employee/{empId}/checkin → 200 with response DTO")
    void checkIn_returnsOk() throws Exception {
        when(attendanceService.checkIn(eq(EMP_ID), any(AttendanceDTO.class)))
                .thenReturn(sampleResponseDTO);

        mockMvc.perform(post("/api/attendance/employee/{empId}/checkin", EMP_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk());

        verify(attendanceService).checkIn(eq(EMP_ID), any(AttendanceDTO.class));
    }

    @Test
    @DisplayName("POST /employee/{empId}/checkin → 400 when body absent")
    void checkIn_missingBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/attendance/employee/{empId}/checkin", EMP_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/attendance/employee/{empId}/checkout ─────────────────────────

    @Test
    @DisplayName("PUT /employee/{empId}/checkout → 200 with response DTO")
    void checkOut_returnsOk() throws Exception {
        when(attendanceService.checkOut(EMP_ID))
                .thenReturn(sampleResponseDTO);

        mockMvc.perform(put("/api/attendance/employee/{empId}/checkout", EMP_ID))
                .andExpect(status().isOk());

        verify(attendanceService).checkOut(EMP_ID);
    }

    // ── POST /api/attendance/employee/{empId} ─────────────────────────────────

    @Test
    @DisplayName("POST /employee/{empId} → 200 saves attendance record")
    void save_returnsOk() throws Exception {
        when(attendanceService.save(eq(EMP_ID), any(AttendanceDTO.class)))
                .thenReturn(sampleResponseDTO);

        mockMvc.perform(post("/api/attendance/employee/{empId}", EMP_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk());

        verify(attendanceService).save(eq(EMP_ID), any(AttendanceDTO.class));
    }

    // ── PUT /api/attendance/{attendanceId}/regularize ─────────────────────────

    @Test
    @DisplayName("PUT /{attendanceId}/regularize → 200 with regularized record")
    void regularize_returnsOk() throws Exception {
        when(attendanceService.regularize(eq(ATTENDANCE_ID), any(AttendanceDTO.class)))
                .thenReturn(sampleResponseDTO);

        mockMvc.perform(put("/api/attendance/{attendanceId}/regularize", ATTENDANCE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk());

        verify(attendanceService).regularize(eq(ATTENDANCE_ID), any(AttendanceDTO.class));
    }

    // ── GET /api/attendance/employee/{empId}/present-count ────────────────────

    @Test
    @DisplayName("GET /employee/{empId}/present-count → 200 with count")
    void getPresentCount_returnsOk() throws Exception {
        when(attendanceService.getPresentCount(EMP_ID, FROM, TO)).thenReturn(20);

        mockMvc.perform(get("/api/attendance/employee/{empId}/present-count", EMP_ID)
                        .param("from", FROM.toString())
                        .param("to",   TO.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));

        verify(attendanceService).getPresentCount(EMP_ID, FROM, TO);
    }

    @Test
    @DisplayName("GET /employee/{empId}/present-count → 400 when params missing")
    void getPresentCount_missingParams_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/{empId}/present-count", EMP_ID))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/attendance ───────────────────────────────────────────────────

//    @Test
//    @DisplayName("GET /api/attendance → 200 with full attendance list")
//    void getAll_returnsOk() throws Exception {
//        AttendanceResponse ar = new AttendanceResponse();
//        when(attendanceService.getAll()).thenReturn(List.of(ar));
//
//        mockMvc.perform(get("/api/attendance"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//
//        verify(attendanceService).getAll();
//    }

    // ── GET /api/attendance/summary ───────────────────────────────────────────

//    @Test
//    @DisplayName("GET /summary → 200 with summary map list")
//    void getSummary_returnsOk() throws Exception {
//        Map<String, Object> entry = Map.of("empId", 1, "present", 20);
//        when(attendanceService.getAttendanceSummary()).thenReturn(List.of(entry));
//
//        mockMvc.perform(get("/api/attendance/summary"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].empId").value(1));
//
//        verify(attendanceService).getAttendanceSummary();
//    }

    @Test
    @DisplayName("GET /summary → 200 with empty list")
    void getSummary_empty() throws Exception {
        when(attendanceService.getAttendanceSummary()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/attendance/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/attendance/hr/summary ───────────────────────────────────────

    @Test
    @DisplayName("GET /hr/summary → 200 with HR summary response")
    void getHrSummary_returnsOk() throws Exception {
        AttendanceSummaryResponse summary = new AttendanceSummaryResponse();
        when(attendanceService.getSummary(FROM, TO)).thenReturn(summary);

        mockMvc.perform(get("/api/attendance/hr/summary")
                        .param("from", FROM.toString())
                        .param("to",   TO.toString()))
                .andExpect(status().isOk());

        verify(attendanceService).getSummary(FROM, TO);
    }

    @Test
    @DisplayName("GET /hr/summary → 400 when params missing")
    void getHrSummary_missingParams_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/attendance/hr/summary"))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/attendance/export ────────────────────────────────────────────

//    @Test
//    @DisplayName("GET /export → 200 with CSV content-type and disposition header")
//    void export_returnsCsvFile() throws Exception {
//        byte[] csvData = "empId,date,status\n1,2025-01-01,PRESENT".getBytes();
//        when(attendanceService.exportAttendanceAsCsv()).thenReturn(csvData);
//
//        mockMvc.perform(get("/api/attendance/export"))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Content-Disposition",
//                        "attachment; filename=\"attendance_export.csv\""))
//                .andExpect(content().contentType("text/csv"));
//
//        verify(attendanceService).exportAttendanceAsCsv();
//    }

//    @Test
//    @DisplayName("GET /export → 200 with non-empty body")
//    void export_bodyNotEmpty() throws Exception {
//        byte[] csvData = "empId,date\n1,2025-01-01".getBytes();
//        when(attendanceService.exportAttendanceAsCsv()).thenReturn(csvData);
//
//        mockMvc.perform(get("/api/attendance/export"))
//                .andExpect(status().isOk())
//                .andExpect(content().bytes(csvData));
//    }
}