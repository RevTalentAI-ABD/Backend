package com.revtalent.revtalent.dto.employee;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponse {

    private Long id;

    private String employeeCode;

    private String designation;
    private String status;

    private String departmentName;

}