package com.revtalent.revtalent.dto;

import com.revtalent.revtalent.model.Employee;
import lombok.Data;

@Data
public class UpdateEmployeeRequest {


    private String designation;
    private String phone;
    private String address;
    private Long departmentId;
    private String status; // "ACTIVE", "INACTIVE", "ON_LEAVE"

    public Employee.Status parsedStatus() {
        if (status == null || status.isBlank()) return null;
        try {
            return Employee.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status
                    + ". Allowed: ACTIVE, INACTIVE, ON_LEAVE");
        }
    }
}