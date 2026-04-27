package com.revtalent.revtalent.model;

import jakarta.persistence.*;
import lombok.*;

        import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_payroll_month",
                        columnNames = {"employee_id", "pay_month", "pay_year"})
        },
        indexes = {
                @Index(name = "idx_payroll_period", columnList = "pay_year, pay_month")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_payroll_employee"))
    private Employee employee;

    @Column(name = "pay_month", nullable = false)
    private Integer payMonth;

    @Column(name = "pay_year", nullable = false)
    private Integer payYear;

    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal hra = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "pf_deduction", nullable = false, precision = 12, scale = 2)
    private BigDecimal pfDeduction = BigDecimal.ZERO;

    @Column(name = "tax_deduction", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxDeduction = BigDecimal.ZERO;

    // Mirrors the MySQL GENERATED column — read-only from DB
    @Column(name = "net_pay", insertable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal netPay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "ENUM('PENDING','PROCESSED','PAID') DEFAULT 'PENDING'")
    private Status status = Status.PENDING;

    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        processedAt = LocalDateTime.now();
    }

    public enum Status {
        PENDING, PROCESSED, PAID
    }
}
