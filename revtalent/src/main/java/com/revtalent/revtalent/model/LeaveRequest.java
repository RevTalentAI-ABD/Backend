package com.revtalent.revtalent.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_request",
        indexes = {
                @Index(name = "idx_leave_emp_status", columnList = "employee_id, status"),
                @Index(name = "idx_leave_dates",      columnList = "start_date, end_date")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_leave_employee"))
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by",
            foreignKey = @ForeignKey(name = "fk_leave_approved_by"))
    private Employee approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false,
            columnDefinition = "ENUM('ANNUAL','SICK','CASUAL','MATERNITY','PATERNITY','UNPAID')")
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days", nullable = false, precision = 4, scale = 1)
    private BigDecimal totalDays = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "ENUM('APPLIED','APPROVED','REJECTED','CANCELLED') DEFAULT 'APPLIED'")
    private Status status = Status.APPLIED;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }

    public enum LeaveType {
        ANNUAL, SICK, CASUAL, MATERNITY, PATERNITY, UNPAID
    }

    public enum Status {
        APPLIED, APPROVED, REJECTED, CANCELLED
    }
}
