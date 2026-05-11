package com.revtalent.revtalent.dto.leave;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

import lombok.*;
import java.time.LocalDate;

@Data
public class LeaveApplyDTO {
    private Long employeeId;
    private String leaveType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    private String reason;
}
