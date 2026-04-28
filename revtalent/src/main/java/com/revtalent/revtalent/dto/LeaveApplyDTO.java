package com.revtalent.revtalent.dto;

import lombok.*;
import java.time.LocalDate;

@Data
public class LeaveApplyDTO {
    private Long employeeId;
    private String leaveType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
}