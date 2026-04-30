package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.employee.ManagerProfileResponse;
import com.revtalent.revtalent.service.EmployeeService;
import com.revtalent.revtalent.service.ManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DashboardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)

@DisplayName("DashboardController – MockMvc Tests")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ManagerService managerService;
    @MockBean private EmployeeService employeeService;

    @Nested
    @DisplayName("GET /api/manager/dashboard")
    class GetDashboard {

        @Test
        @DisplayName("returns 200 with JSON dashboard map")
        void returns200WithDashboard() throws Exception {
            Map<String, Object> stub = new LinkedHashMap<>();
            stub.put("teamSize", 20);
            stub.put("present", 10);
            stub.put("wfh", 3);
            stub.put("absent", 2);
            stub.put("onLeave", 5);
            stub.put("pendingLeaves", 4);
            when(managerService.getDashboard()).thenReturn(stub);

            mockMvc.perform(get("/api/manager/dashboard").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.teamSize").value(20))
                    .andExpect(jsonPath("$.present").value(10))
                    .andExpect(jsonPath("$.wfh").value(3))
                    .andExpect(jsonPath("$.absent").value(2))
                    .andExpect(jsonPath("$.onLeave").value(5))
                    .andExpect(jsonPath("$.pendingLeaves").value(4));
        }

        @Test
        @DisplayName("delegates to ManagerService.getDashboard() exactly once")
        void delegatesToService() throws Exception {
            when(managerService.getDashboard()).thenReturn(Map.of());

            mockMvc.perform(get("/api/manager/dashboard"));

            verify(managerService, times(1)).getDashboard();
        }
    }

    @Nested
    @DisplayName("GET /api/manager/activity")
    class GetActivity {

        @Test
        @DisplayName("returns 200 with list of activity maps")
        void returns200WithActivity() throws Exception {
            Map<String, Object> activity = new LinkedHashMap<>();
            activity.put("icon", "📄");
            activity.put("text", "alice applied for sick leave");
            activity.put("time", "2 hr ago");
            activity.put("type", "LEAVE");
            when(managerService.getActivity()).thenReturn(List.of(activity));

            mockMvc.perform(get("/api/manager/activity").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].type").value("LEAVE"))
                    .andExpect(jsonPath("$[0].time").value("2 hr ago"));
        }

        @Test
        @DisplayName("returns 200 with empty array when no activities")
        void returns200EmptyArray() throws Exception {
            when(managerService.getActivity()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/activity").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/manager/profile")
    class GetProfile {

        @Test
        @DisplayName("returns 200 with manager profile JSON")
        void returns200WithProfile() throws Exception {
            ManagerProfileResponse profile = ManagerProfileResponse.builder()
                    .id(1L)
                    .name("Alice Manager")
                    .username("alice")
                    .email("alice@revtalent.com")
                    .designation("Engineering Manager")
                    .department("Engineering")
                    .employeeCode("MGR001")
                    .phone("9876543210")
                    .gender("Female")
                    .joiningDate(LocalDate.of(2020, 1, 10))
                    .status("ACTIVE")
                    .teamSize(8)
                    .build();
            when(employeeService.getManagerProfile(1L)).thenReturn(profile);

            mockMvc.perform(get("/api/manager/profile").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Alice Manager"))
                    .andExpect(jsonPath("$.username").value("alice"))
                    .andExpect(jsonPath("$.email").value("alice@revtalent.com"))
                    .andExpect(jsonPath("$.designation").value("Engineering Manager"))
                    .andExpect(jsonPath("$.department").value("Engineering"))
                    .andExpect(jsonPath("$.teamSize").value(8))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("calls getManagerProfile with hardcoded id=1")
        void callsServiceWithId1() throws Exception {
            when(employeeService.getManagerProfile(1L))
                    .thenReturn(ManagerProfileResponse.builder().id(1L).status("ACTIVE").teamSize(0).build());

            mockMvc.perform(get("/api/manager/profile"));

            verify(employeeService).getManagerProfile(1L);
        }
    }
}