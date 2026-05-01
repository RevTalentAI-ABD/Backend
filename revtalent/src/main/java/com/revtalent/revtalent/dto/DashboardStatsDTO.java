package com.revtalent.revtalent.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private int leaveBalance;
    private double attendancePercentage;
    private String nextPayslipDate;
    private double hoursThisWeek;
}