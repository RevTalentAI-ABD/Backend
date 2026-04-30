package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.attendance.AttendanceResponse;
import com.revtalent.revtalent.service.AttendanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AttendanceController – MockMvc Tests")
class AttendanceControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private AttendanceService attendanceService;

    private AttendanceResponse sampleResponse() {
        return AttendanceResponse.builder()
                .id(1L)
                .employeeId(1L)
                .employeeName("johndoe")
                .department("Engineering")
                .workDate(LocalDate.of(2025, 5, 1))
                .checkIn(LocalDateTime.of(2025, 5, 1, 9, 0))
                .checkOut(LocalDateTime.of(2025, 5, 1, 18, 0))
                .durationMin(540)
                .attendanceType("WFO")
                .status("PRESENT")
                .isRegularized(false)
                .notes("On time")
                .build();
    }

    @Nested
    @DisplayName("GET /api/manager/attendance")
    class GetAttendance {

        @Test
        @DisplayName("returns 200 with attendance list")
        void returns200WithList() throws Exception {
            when(attendanceService.getAttendance()).thenReturn(List.of(sampleResponse()));

            mockMvc.perform(get("/api/manager/attendance").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].employeeName").value("johndoe"))
                    .andExpect(jsonPath("$[0].status").value("PRESENT"))
                    .andExpect(jsonPath("$[0].department").value("Engineering"))
                    .andExpect(jsonPath("$[0].durationMin").value(540));
        }

        @Test
        @DisplayName("returns 200 with empty list when no attendance records")
        void returns200EmptyList() throws Exception {
            when(attendanceService.getAttendance()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/attendance").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("delegates to attendanceService.getAttendance() once")
        void delegatesToService() throws Exception {
            when(attendanceService.getAttendance()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/attendance"));

            verify(attendanceService, times(1)).getAttendance();
        }
    }
    @Nested
    @DisplayName("GET /api/manager/attendance/summary")
    class GetSummary {

        @Test
        @DisplayName("returns 200 with weekly summary list")
        void returns200WithSummary() throws Exception {
            Map<String, Object> weekSummary = Map.of(
                    "week", "2025-04-28",
                    "present", 10L,
                    "absent", 2L,
                    "leave", 1L,
                    "wfh", 3L
            );
            when(attendanceService.getAttendanceSummary()).thenReturn(List.of(weekSummary));

            mockMvc.perform(get("/api/manager/attendance/summary").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].week").value("2025-04-28"))
                    .andExpect(jsonPath("$[0].present").value(10));
        }

        @Test
        @DisplayName("returns 200 with empty list when no data")
        void returns200Empty() throws Exception {
            when(attendanceService.getAttendanceSummary()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/attendance/summary").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/manager/attendance/export")
    class Export {

        @Test
        @DisplayName("returns 200 with text/csv content type and attachment header")
        void returnsCsvWithCorrectHeaders() throws Exception {
            String csv = "ID,Employee ID,Employee Name\n1,1,johndoe\n";
            when(attendanceService.exportAttendanceAsCsv()).thenReturn(csv.getBytes());

            mockMvc.perform(get("/api/manager/attendance/export"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition",
                            containsString("attachment")))
                    .andExpect(header().string("Content-Disposition",
                            containsString("attendance_export.csv")))
                    .andExpect(content().contentTypeCompatibleWith("text/csv"));
        }

        @Test
        @DisplayName("returns CSV bytes matching service output")
        void returnsCsvBytes() throws Exception {
            byte[] data = "ID,Name\n1,johndoe\n".getBytes();
            when(attendanceService.exportAttendanceAsCsv()).thenReturn(data);

            byte[] responseBody = mockMvc.perform(get("/api/manager/attendance/export"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsByteArray();

            assertThat(responseBody).isEqualTo(data);
        }
    }
}