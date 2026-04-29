package com.revtalent.revtalent.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerProfileResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String designation;
    private String department;
    private String employeeCode;
    private String phone;
    private String gender;
    private LocalDate joiningDate;
    private LocalDate dateOfBirth;
    private String address;
    private String profilePictureUrl;
    private String status;
    private int teamSize;
}