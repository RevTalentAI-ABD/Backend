package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.PayrollDTO;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.Payroll;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    // Get all payrolls for an employee
    public List<Payroll> getByEmployee(Long empId) {
        return payrollRepository.findByEmployee_IdOrderByPayYearDescPayMonthDesc(empId);
    }

    // Get payroll for a specific month/year
    public Payroll getByEmployeeAndMonth(Long empId, int month, int year) {
        return payrollRepository.findByEmployee_IdAndPayMonthAndPayYear(empId, month, year)
                .orElseThrow(() -> new RuntimeException(
                        "Payroll not found for employee " + empId + " month " + month + "/" + year));
    }

    // Get all payrolls for a specific month/year (HR view)
    public List<Payroll> getByMonth(int month, int year) {
        return payrollRepository.findByPayMonthAndPayYear(month, year);
    }

    // Get by status for an employee
    public List<Payroll> getByStatus(Long empId, Payroll.Status status) {
        return payrollRepository.findByEmployee_IdAndStatus(empId, status);
    }

    // Create payroll for an employee
    public Payroll create(Long empId, PayrollDTO dto) {
        // Prevent duplicate payroll for same month/year
        payrollRepository.findByEmployee_IdAndPayMonthAndPayYear(empId, dto.getPayMonth(), dto.getPayYear())
                .ifPresent(p -> { throw new RuntimeException(
                        "Payroll already exists for month " + dto.getPayMonth() + "/" + dto.getPayYear()); });

        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        Payroll payroll = buildPayroll(emp, dto);
        return payrollRepository.save(payroll);
    }

    // Update payroll
    public Payroll update(Long payrollId, PayrollDTO dto) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found: " + payrollId));

        if (payroll.getStatus() == Payroll.Status.PAID) {
            throw new RuntimeException("Cannot update a payroll that is already PAID");
        }

        payroll.setBasicSalary(dto.getBasicSalary());
        payroll.setHra(dto.getHra());
        payroll.setAllowances(dto.getAllowances());
        payroll.setDeductions(dto.getDeductions());
        payroll.setPfDeduction(dto.getPfDeduction());
        payroll.setTaxDeduction(dto.getTaxDeduction());

        // Calculate net salary in transient field
        payroll.setNetSalary(calculateNet(dto));

        if (dto.getStatus() != null) {
            payroll.setStatus(dto.getStatus());
        }

        return payrollRepository.save(payroll);
    }

    // Mark payroll as PROCESSED
    public Payroll process(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found: " + payrollId));

        if (payroll.getStatus() != Payroll.Status.PENDING) {
            throw new RuntimeException("Only PENDING payrolls can be processed");
        }

        payroll.setStatus(Payroll.Status.PROCESSED);
        return payrollRepository.save(payroll);
    }

    // Mark payroll as PAID
    public Payroll markPaid(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found: " + payrollId));

        if (payroll.getStatus() != Payroll.Status.PROCESSED) {
            throw new RuntimeException("Only PROCESSED payrolls can be marked as PAID");
        }

        payroll.setStatus(Payroll.Status.PAID);
        return payrollRepository.save(payroll);
    }

    // Bulk process all PENDING payrolls for a month
    public List<Payroll> bulkProcess(int month, int year) {
        List<Payroll> pending = payrollRepository.findByPayMonthAndPayYear(month, year)
                .stream()
                .filter(p -> p.getStatus() == Payroll.Status.PENDING)
                .toList();

        pending.forEach(p -> p.setStatus(Payroll.Status.PROCESSED));
        return payrollRepository.saveAll(pending);
    }

    // Delete payroll (only PENDING)
    public void delete(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found: " + payrollId));

        if (payroll.getStatus() != Payroll.Status.PENDING) {
            throw new RuntimeException("Only PENDING payrolls can be deleted");
        }

        payrollRepository.deleteById(payrollId);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Payroll buildPayroll(Employee emp, PayrollDTO dto) {
        Payroll p = Payroll.builder()
                .employee(emp)
                .payMonth(dto.getPayMonth())
                .payYear(dto.getPayYear())
                .basicSalary(orZero(dto.getBasicSalary()))
                .hra(orZero(dto.getHra()))
                .allowances(orZero(dto.getAllowances()))
                .deductions(orZero(dto.getDeductions()))
                .pfDeduction(orZero(dto.getPfDeduction()))
                .taxDeduction(orZero(dto.getTaxDeduction()))
                .status(dto.getStatus() != null ? dto.getStatus() : Payroll.Status.PENDING)
                .build();

        p.setNetSalary(calculateNet(dto));
        return p;
    }

    private BigDecimal calculateNet(PayrollDTO dto) {
        return orZero(dto.getBasicSalary())
                .add(orZero(dto.getHra()))
                .add(orZero(dto.getAllowances()))
                .subtract(orZero(dto.getDeductions()))
                .subtract(orZero(dto.getPfDeduction()))
                .subtract(orZero(dto.getTaxDeduction()));
    }

    private BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}