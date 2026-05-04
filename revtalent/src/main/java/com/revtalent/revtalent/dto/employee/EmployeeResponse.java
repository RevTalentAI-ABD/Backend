package com.revtalent.revtalent.dto.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String name;
    private String username;
    private String email;
    private String role;
    private String designation;
    private String status;
    private String departmentName;
    private Long departmentId;
    private Long managerId;
    private String phone;
    private String address;
    private String profilePictureUrl;
    private LocalDate joiningDate;
    private LocalDate dateOfBirth;
    private String gender;
}