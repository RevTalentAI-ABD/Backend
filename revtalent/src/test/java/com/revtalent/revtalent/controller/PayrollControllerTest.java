package com.revtalent.revtalent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revtalent.revtalent.exception.GlobalExceptionHandler;
import com.revtalent.revtalent.model.Payroll;
import com.revtalent.revtalent.service.PayrollService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayrollController.class)
@Import(GlobalExceptionHandler.class)
class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PayrollService payrollService;



    @Test
    @WithMockUser
    void getPayslips_whenExists_returns200() throws Exception {
        Payroll payroll = new Payroll();
        payroll.setId(1L);
        payroll.setPayMonth(4);
        payroll.setPayYear(2026);
        payroll.setBasicSalary(BigDecimal.valueOf(50000));
        payroll.setStatus(Payroll.Status.PAID);

        when(payrollService.getByEmployee(1L)).thenReturn(List.of(payroll));

        mockMvc.perform(get("/api/payroll/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].payMonth").value(4))
                .andExpect(jsonPath("$[0].status").value("PAID"));
    }

    @Test
    @WithMockUser
    void getPayslips_whenEmployeeNotFound_returns404() throws Exception {
        when(payrollService.getByEmployee(99L))
                .thenThrow(new RuntimeException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/payroll/employee/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }



    @Test
    @WithMockUser
    void getMonthPayslip_whenExists_returns200() throws Exception {
        Payroll payroll = new Payroll();
        payroll.setId(1L);
        payroll.setPayMonth(4);
        payroll.setPayYear(2026);
        payroll.setStatus(Payroll.Status.PENDING);

        when(payrollService.getByEmployeeAndMonth(1L, 4, 2026)).thenReturn(payroll);

        mockMvc.perform(get("/api/payroll/employee/1/month")
                        .param("month", "4")
                        .param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payMonth").value(4))
                .andExpect(jsonPath("$.payYear").value(2026));
    }

    @Test
    @WithMockUser
    void getMonthPayslip_whenNotFound_returns404() throws Exception {
        when(payrollService.getByEmployeeAndMonth(1L, 4, 2026))
                .thenThrow(new RuntimeException("Payroll not found for employee 1 month 4/2026"));

        mockMvc.perform(get("/api/payroll/employee/1/month")
                        .param("month", "4")
                        .param("year", "2026"))
                .andExpect(status().isNotFound());
    }
}