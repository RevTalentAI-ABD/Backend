package com.revtalent.revtalent.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSummaryResponse {
    private long totalEmployees;
    private long present;
    private long absent;
    private long wfh;
    private long onLeave;
    private long field;
}