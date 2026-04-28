package com.revtalent.revtalent.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateEmployeeRequest {
    // User fields
    private String name;
    private String username;
    private String email;
    private String password;
    private String role;        // "EMPLOYEE", "MANAGER", "HR_ADMIN"

    // Employee fields
    private String employeeCode;
    private String designation;
    private LocalDate joiningDate;
    private String phone;
    private String address;
    private Long departmentId;
}