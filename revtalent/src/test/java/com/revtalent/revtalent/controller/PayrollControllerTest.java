package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.payroll.PayrollResponse;
import com.revtalent.revtalent.model.Payroll;
import com.revtalent.revtalent.service.PayrollService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayrollController.class)
class PayrollControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private PayrollService service;

    private PayrollResponse payrollResponse;

    @BeforeEach
    void setUp() {
        payrollResponse = new PayrollResponse();
        payrollResponse.setId(1L);
        payrollResponse.setEmployeeId(1L);
        payrollResponse.setEmployeeName("John Doe");
        payrollResponse.setEmployeeCode("EMP001");
        payrollResponse.setDepartmentName("Engineering");
        payrollResponse.setPayMonth(4);
        payrollResponse.setPayYear(2025);
        payrollResponse.setBasicSalary(BigDecimal.valueOf(50000));
        payrollResponse.setStatus(Payroll.Status.PENDING);
    }

    // ================= GET =================

    @Test
    @WithMockUser
    void getAll_returns200WithList() throws Exception {

        when(service.getAll()).thenReturn(List.of(payrollResponse));

        mockMvc.perform(get("/api/payroll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void getAll_emptyList_returns200() throws Exception {

        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/payroll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void getByEmployee_returns200WithList() throws Exception {

        when(service.getByEmployee(1L)).thenReturn(List.of(payrollResponse));

        mockMvc.perform(get("/api/payroll/employee/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getByEmployee_notFound_returns400() throws Exception {

        when(service.getByEmployee(99L))
                .thenThrow(new RuntimeException("Employee not found"));

        mockMvc.perform(get("/api/payroll/employee/99"))
                .andExpect(status().isBadRequest()); // ✅ FIXED
    }

    // ================= POST =================

    @Test
    @WithMockUser(roles = "HR_ADMIN")
    void generate_returns200WithList() throws Exception {

        when(service.generatePayroll(4, 2025)).thenReturn(List.of(payrollResponse));

        mockMvc.perform(post("/api/payroll/generate")
                        .with(csrf()) // ✅ IMPORTANT
                        .param("month", "4")
                        .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HR_ADMIN")
    void process_returns200WithUpdatedStatus() throws Exception {

        payrollResponse.setStatus(Payroll.Status.PROCESSED);
        when(service.processPayroll()).thenReturn(List.of(payrollResponse));

        mockMvc.perform(post("/api/payroll/process")
                        .with(csrf())) // ✅ IMPORTANT
                .andExpect(status().isOk());
    }

    // ================= SECURITY =================

    @Test
    void getAll_noAuth_returns401() throws Exception {

        mockMvc.perform(get("/api/payroll"))
                .andExpect(status().isUnauthorized());
    }
}