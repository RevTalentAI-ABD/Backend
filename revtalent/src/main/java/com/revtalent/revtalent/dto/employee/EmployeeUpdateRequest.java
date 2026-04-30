package com.revtalent.revtalent.dto.employee;

import lombok.Data;

@Data
public class EmployeeUpdateRequest {

    private String designation;
    private String phone;
    private String address;
}