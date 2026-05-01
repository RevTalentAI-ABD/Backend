package com.revtalent.revtalent.dto.leave;

import com.revtalent.revtalent.model.LeaveRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveHistoryDTO {
    private Long id;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDays;
    private String status;
    private String reason;
    private String rejectionReason;
    private LocalDateTime appliedAt;
    private LocalDateTime actionedAt;
    private String approvedByName; // Only the name, nothing else

    // Static factory method — clean mapping from entity
    public static LeaveHistoryDTO from(LeaveRequest leave) {
        LeaveHistoryDTO dto = new LeaveHistoryDTO();
        dto.setId(leave.getId());
        dto.setLeaveType(leave.getLeaveType().name());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setTotalDays(leave.getTotalDays());
        dto.setStatus(leave.getStatus().name());
        dto.setReason(leave.getReason());
        dto.setRejectionReason(leave.getRejectionReason());
        dto.setAppliedAt(leave.getAppliedAt());
        dto.setActionedAt(leave.getActionedAt());

        // Safely extract approvedBy name without loading full object
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getUser() != null) {
            dto.setApprovedByName(leave.getApprovedBy().getUser().getName());
        }

        return dto;
    }
}