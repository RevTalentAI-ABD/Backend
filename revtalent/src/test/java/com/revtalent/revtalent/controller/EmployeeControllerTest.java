package com.revtalent.revtalent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revtalent.revtalent.exception.GlobalExceptionHandler;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    // ── GET /api/employees/{id} ───────────────────────────────────────────────

    @Test
    @WithMockUser
    void getEmployee_whenExists_returns200() throws Exception {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setEmployeeCode("EMP001");
        emp.setDesignation("Software Engineer");

        when(employeeService.getEmployeeById(1L)).thenReturn(emp);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeCode").value("EMP001"));
    }

    @Test
    @WithMockUser
    void getEmployee_whenNotExists_returns404() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new RuntimeException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }

    @Test
    @WithMockUser
    void getEmployee_whenWrongIdType_returns400() throws Exception {
        mockMvc.perform(get("/api/employees/abc"))
                .andExpect(status().isBadRequest());
    }
}