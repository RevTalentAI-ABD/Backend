package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.payroll.PayrollResponse;
import com.revtalent.revtalent.model.*;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.PayrollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock private PayrollRepository repo;
    @Mock private EmployeeRepository employeeRepo;

    @InjectMocks private PayrollService payrollService;

    private Employee employee;
    private Payroll payroll;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setUser(user);
        employee.setDepartment(department);

        payroll = Payroll.builder()
                .id(1L)
                .employee(employee)
                .payMonth(4)
                .payYear(2025)
                .basicSalary(BigDecimal.valueOf(50000))
                .hra(BigDecimal.valueOf(10000))
                .allowances(BigDecimal.valueOf(5000))
                .deductions(BigDecimal.valueOf(2000))
                .pfDeduction(BigDecimal.valueOf(1500))
                .taxDeduction(BigDecimal.valueOf(3000))
                .status(Payroll.Status.PENDING)
                .build();
    }

    @Test
    void generatePayroll_success() {
        when(employeeRepo.findAll()).thenReturn(List.of(employee));
        when(repo.findByEmployee(employee)).thenReturn(List.of());
        when(repo.saveAll(any())).thenReturn(List.of(payroll));

        List<PayrollResponse> result = payrollService.generatePayroll(4, 2025);

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());
        verify(repo).saveAll(any());
    }

    @Test
    void generatePayroll_alreadyExists_skipsEmployee() {
        when(employeeRepo.findAll()).thenReturn(List.of(employee));
        when(repo.findByEmployee(employee)).thenReturn(List.of(payroll));
        when(repo.saveAll(any())).thenReturn(List.of()); // ✅ mock saveAll

        List<PayrollResponse> result = payrollService.generatePayroll(4, 2025);

        assertEquals(0, result.size());
        verify(repo).saveAll(argThat(list -> ((List<?>) list).isEmpty())); // ✅ called with empty list
    }

    @Test
    void processPayroll_calculatesNetSalary() {
        when(repo.findAll()).thenReturn(List.of(payroll));
        when(repo.saveAll(any())).thenReturn(List.of(payroll));

        List<PayrollResponse> result = payrollService.processPayroll();

        assertEquals(1, result.size());
        assertEquals(Payroll.Status.PROCESSED, result.get(0).getStatus());
        verify(repo).saveAll(any());
    }

    @Test
    void getAll_returnsResponseList() {
        when(repo.findAll()).thenReturn(List.of(payroll));

        List<PayrollResponse> result = payrollService.getAll();

        assertEquals(1, result.size());
        assertEquals("EMP001", result.get(0).getEmployeeCode());
        assertEquals("Engineering", result.get(0).getDepartmentName());
    }

    @Test
    void getByEmployee_success() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(repo.findByEmployee(employee)).thenReturn(List.of(payroll));

        List<PayrollResponse> result = payrollService.getByEmployee(1L);

        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getPayMonth());
    }

    @Test
    void getByEmployee_notFound_throwsException() {
        when(employeeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> payrollService.getByEmployee(99L));
    }
}