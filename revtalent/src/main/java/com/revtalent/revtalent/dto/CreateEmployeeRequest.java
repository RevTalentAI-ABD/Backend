package com.revtalent.revtalent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEmployeeRequest {

    // ── User fields ──────────────────────────────────────────────────────────

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    /** Allowed values: EMPLOYEE, MANAGER, HR_ADMIN  (defaults to EMPLOYEE if omitted) */
    private String role;

    // ── Employee fields ──────────────────────────────────────────────────────

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private String phone;

    private String address;

    private Long departmentId;
    private String gender;
}