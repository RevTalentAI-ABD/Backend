package com.revtalent.revtalent.dto;

import com.revtalent.revtalent.model.Employee;
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
    private LocalDate joiningDate;
    private String phone;
    private String address;
    private String profilePictureUrl;
    private String status;

    // Flattened user info (no password, no roles exposed)
    private String name;
    private String username;
    private String email;
    private String role;

    // Department info (minimal)
    private Long departmentId;
    private String departmentName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EmployeeResponse from(Employee emp) {
        EmployeeResponse.EmployeeResponseBuilder builder = EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .designation(emp.getDesignation())
                .joiningDate(emp.getJoiningDate())
                .phone(emp.getPhone())
                .address(emp.getAddress())
                .profilePictureUrl(emp.getProfilePictureUrl())
                .status(emp.getStatus() != null ? emp.getStatus().name() : null)
                .createdAt(emp.getCreatedAt())
                .updatedAt(emp.getUpdatedAt());

        if (emp.getUser() != null) {
            builder.name(emp.getUser().getName())
                    .username(emp.getUser().getUsername())
                    .email(emp.getUser().getEmail())
                    .role(emp.getUser().getRole() != null ? emp.getUser().getRole().name() : null);
        }

        if (emp.getDepartment() != null) {
            builder.departmentId(emp.getDepartment().getId())
                    .departmentName(emp.getDepartment().getName());
        }

        return builder.build();
    }
}