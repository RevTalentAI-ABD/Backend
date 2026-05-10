package com.revtalent.revtalent.dto.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String designation;
    private String status;
    private String departmentName;
    private Long departmentId;

    // User info
    private String name;
    private String username;
    private String email;
    private String role;

    // Additional fields
    private String phone;
    private String address;
    private String profilePictureUrl;
    private LocalDate joiningDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long managerId;

}