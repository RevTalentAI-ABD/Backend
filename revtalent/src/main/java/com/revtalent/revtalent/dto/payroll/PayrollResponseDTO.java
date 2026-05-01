package com.revtalent.revtalent.dto.payroll;

import com.revtalent.revtalent.model.Payroll;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PayrollResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeName; // convenient for frontend
    private Integer payMonth;
    private Integer payYear;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal pfDeduction;
    private BigDecimal taxDeduction;
    private BigDecimal netPay;
    private Payroll.Status status;
    private LocalDateTime createdAt;

    public static PayrollResponseDTO from(Payroll p) {
        return PayrollResponseDTO.builder()
                .id(p.getId())
                .employeeId(p.getEmployee().getId())
                .employeeName(p.getEmployee().getUser().getName())
                .payMonth(p.getPayMonth())
                .payYear(p.getPayYear())
                .basicSalary(p.getBasicSalary())
                .hra(p.getHra())
                .allowances(p.getAllowances())
                .deductions(p.getDeductions())
                .pfDeduction(p.getPfDeduction())
                .taxDeduction(p.getTaxDeduction())
                .netPay(p.getNetPay())
                .status(p.getStatus())
                .createdAt(p.getProcessedAt())
                .build();
    }
}