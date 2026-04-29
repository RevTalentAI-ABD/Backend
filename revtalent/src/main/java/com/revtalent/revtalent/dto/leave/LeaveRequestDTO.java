package com.revtalent.revtalent.dto.leave;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveRequestDTO {

    private Long employeeId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}