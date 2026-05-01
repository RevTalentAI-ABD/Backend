package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.ManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ReportController – MockMvc Tests")
class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private ManagerService managerService;

    @Nested
    @DisplayName("GET /api/manager/reports/productivity")
    class GetProductivity {

        @Test
        @DisplayName("returns 200 with productivity list from service")
        void returns200WithProductivity() throws Exception {
            Map<String, Object> may = new LinkedHashMap<>();
            may.put("month", "May");
            may.put("score", 85);
            may.put("present", 17L);
            may.put("total", 20L);
            when(managerService.getProductivity()).thenReturn(List.of(may));

            mockMvc.perform(get("/api/manager/reports/productivity").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].month").value("May"))
                    .andExpect(jsonPath("$[0].score").value(85))
                    .andExpect(jsonPath("$[0].present").value(17))
                    .andExpect(jsonPath("$[0].total").value(20));
        }

        @Test
        @DisplayName("returns 200 with empty list when no productivity data")
        void returns200EmptyList() throws Exception {
            when(managerService.getProductivity()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/reports/productivity").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("delegates to managerService.getProductivity() exactly once")
        void delegatesToService() throws Exception {
            when(managerService.getProductivity()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/reports/productivity"));

            verify(managerService, times(1)).getProductivity();
        }
    }

    @Nested
    @DisplayName("GET /api/manager/reports/attendance")
    class GetAttendanceReport {

        @Test
        @DisplayName("returns 200 with attendance report map")
        void returns200WithReport() throws Exception {
            Map<String, Object> report = new LinkedHashMap<>();
            report.put("reportMonth", "MAY 2025");
            report.put("today", Map.of("present", 10L, "wfh", 2L, "absent", 1L, "onLeave", 0L));
            report.put("monthSummary", Map.of("attendanceRate", "88%"));
            report.put("perEmployee", List.of());
            when(managerService.getAttendanceReport()).thenReturn(report);

            mockMvc.perform(get("/api/manager/reports/attendance").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reportMonth").value("MAY 2025"))
                    .andExpect(jsonPath("$.monthSummary.attendanceRate").value("88%"))
                    .andExpect(jsonPath("$.today.present").value(10))
                    .andExpect(jsonPath("$.perEmployee", hasSize(0)));
        }

        @Test
        @DisplayName("delegates to managerService.getAttendanceReport() exactly once")
        void delegatesToService() throws Exception {
            when(managerService.getAttendanceReport()).thenReturn(Map.of());

            mockMvc.perform(get("/api/manager/reports/attendance"));

            verify(managerService, times(1)).getAttendanceReport();
        }
    }

    @Nested
    @DisplayName("GET /api/manager/reports/team-summary")
    class GetTeamSummary {

        @Test
        @DisplayName("returns 200 with team summary map")
        void returns200WithTeamSummary() throws Exception {
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("totalEmployees", 30L);
            summary.put("activeEmployees", 25L);
            summary.put("onLeave", 3L);
            summary.put("inactive", 2L);
            summary.put("pendingLeaves", 5L);
            summary.put("approvedLeaves", 8L);
            summary.put("presentToday", 15L);
            summary.put("wfhToday", 5L);
            summary.put("absentToday", 2L);
            when(managerService.getTeamSummary()).thenReturn(summary);

            mockMvc.perform(get("/api/manager/reports/team-summary").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEmployees").value(30))
                    .andExpect(jsonPath("$.activeEmployees").value(25))
                    .andExpect(jsonPath("$.onLeave").value(3))
                    .andExpect(jsonPath("$.inactive").value(2))
                    .andExpect(jsonPath("$.pendingLeaves").value(5))
                    .andExpect(jsonPath("$.presentToday").value(15))
                    .andExpect(jsonPath("$.wfhToday").value(5))
                    .andExpect(jsonPath("$.absentToday").value(2));
        }

        @Test
        @DisplayName("returns 200 with empty map when service returns empty")
        void returns200WithEmptyMap() throws Exception {
            when(managerService.getTeamSummary()).thenReturn(Map.of());

            mockMvc.perform(get("/api/manager/reports/team-summary").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("delegates to managerService.getTeamSummary() exactly once")
        void delegatesToService() throws Exception {
            when(managerService.getTeamSummary()).thenReturn(Map.of());

            mockMvc.perform(get("/api/manager/reports/team-summary"));

            verify(managerService, times(1)).getTeamSummary();
        }
    }
}