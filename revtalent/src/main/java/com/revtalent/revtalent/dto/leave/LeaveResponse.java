package com.revtalent.revtalent.dto.leave;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LeaveResponse {

    private Long id;
    private String leaveType;
    private String status;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
}