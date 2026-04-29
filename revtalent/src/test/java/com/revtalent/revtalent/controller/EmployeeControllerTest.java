package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.employee.EmployeeResponse;
import com.revtalent.revtalent.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EmployeeController – MockMvc Tests")
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private EmployeeService employeeService;

    private EmployeeResponse emp(Long id, String name) {
        return EmployeeResponse.builder()
                .id(id).name(name)
                .designation("Engineer").department("Engineering")
                .status("ACTIVE").employeeCode("EMP00" + id).phone("987654321" + id)
                .build();
    }

    @Nested
    @DisplayName("GET /api/manager/team")
    class GetTeam {

        @Test
        @DisplayName("returns 200 with all employees")
        void returns200WithTeam() throws Exception {
            when(employeeService.getTeam()).thenReturn(List.of(emp(1L, "Alice"), emp(2L, "Bob")));

            mockMvc.perform(get("/api/manager/team").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Alice"))
                    .andExpect(jsonPath("$[1].name").value("Bob"));
        }

        @Test
        @DisplayName("returns 200 with empty array when team is empty")
        void returns200EmptyTeam() throws Exception {
            when(employeeService.getTeam()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/team").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/manager/team/{id}")
    class GetEmployeeById {

        @Test
        @DisplayName("returns 200 with employee for valid id")
        void returns200ForValidId() throws Exception {
            when(employeeService.getEmployeeById(1L)).thenReturn(emp(1L, "Alice"));

            mockMvc.perform(get("/api/manager/team/1").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Alice"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("returns 404 when employee not found")
        void returns404WhenNotFound() throws Exception {
            when(employeeService.getEmployeeById(99L))
                    .thenThrow(new RuntimeException("Employee not found with id: 99"));

            mockMvc.perform(get("/api/manager/team/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/manager/team/search")
    class SearchTeam {

        @Test
        @DisplayName("returns 200 with matched employees for query")
        void returns200WithMatches() throws Exception {
            when(employeeService.searchTeam("alice")).thenReturn(List.of(emp(1L, "Alice")));

            mockMvc.perform(get("/api/manager/team/search")
                            .param("query", "alice")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Alice"));
        }

        @Test
        @DisplayName("passes query param to service correctly")
        void passesQueryToService() throws Exception {
            when(employeeService.searchTeam("bob")).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/team/search").param("query", "bob"));

            verify(employeeService).searchTeam("bob");
        }

        @Test
        @DisplayName("returns empty array when no matches found")
        void returnsEmptyForNoMatch() throws Exception {
            when(employeeService.searchTeam("xyz")).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/team/search")
                            .param("query", "xyz")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}