package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.payroll.PayrollResponse;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.Payroll;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository repo;
    private final EmployeeRepository employeeRepo;

    // ✅ Mapper
    private PayrollResponse mapToResponse(Payroll p) {
        PayrollResponse res = new PayrollResponse();
        res.setId(p.getId());
        res.setEmployeeId(p.getEmployee().getId());
        res.setEmployeeName(p.getEmployee().getUser().getName());
        res.setEmployeeCode(p.getEmployee().getEmployeeCode());
        res.setDepartmentName(p.getEmployee().getDepartment().getName());
        res.setPayMonth(p.getPayMonth());
        res.setPayYear(p.getPayYear());
        res.setBasicSalary(p.getBasicSalary());
        res.setHra(p.getHra());
        res.setAllowances(p.getAllowances());
        res.setDeductions(p.getDeductions());
        res.setPfDeduction(p.getPfDeduction());
        res.setTaxDeduction(p.getTaxDeduction());
        res.setNetPay(p.getNetPay());
        res.setStatus(p.getStatus());
        res.setProcessedAt(p.getProcessedAt());
        return res;
    }

    // ✅ Generate Payroll
    public List<PayrollResponse> generatePayroll(int month, int year) {
        List<Employee> employees = employeeRepo.findAll();
        List<Payroll> result = new ArrayList<>();

        for (Employee emp : employees) {
            boolean exists = repo.findByEmployee(emp).stream()
                    .anyMatch(p -> p.getPayMonth() == month && p.getPayYear() == year);

            if (exists) continue;

            Payroll p = Payroll.builder()
                    .employee(emp)
                    .payMonth(month)
                    .payYear(year)
                    .basicSalary(BigDecimal.valueOf(50000))
                    .hra(BigDecimal.valueOf(10000))
                    .allowances(BigDecimal.valueOf(5000))
                    .deductions(BigDecimal.valueOf(2000))
                    .pfDeduction(BigDecimal.valueOf(1500))
                    .taxDeduction(BigDecimal.valueOf(3000))
                    .status(Payroll.Status.PENDING)
                    .build();

            result.add(p);
        }

        return repo.saveAll(result)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Process Payroll
    public List<PayrollResponse> processPayroll() {
        List<Payroll> list = repo.findAll(); // ✅ not findAllWithEmployee()

        for (Payroll p : list) {
            BigDecimal net = p.getBasicSalary()
                    .add(p.getHra())
                    .add(p.getAllowances())
                    .subtract(p.getDeductions())
                    .subtract(p.getPfDeduction())
                    .subtract(p.getTaxDeduction());

            p.setNetSalary(net);
            p.setStatus(Payroll.Status.PROCESSED);
        }

        return repo.saveAll(list)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get All
    public List<PayrollResponse> getAll() {
        return repo.findAll() // ✅ not findAllWithEmployee()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get By Employee
    public List<PayrollResponse> getByEmployee(Long id) {
        Employee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return repo.findByEmployee(emp)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}