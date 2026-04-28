package com.revtalent.revtalent.dto.employee;

import lombok.Data;

@Data
public class EmployeeRequest {
    private String name;
    private String email;
    private Long departmentId;
    private String designation;
}