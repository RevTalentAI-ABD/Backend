package com.revtalent.revtalent.dto;

import lombok.Data;

@Data
public class PatchEmployeeRequest {
    private String phone;
    private String address;
    private String designation;
    private String profilePictureUrl;

    // User personal info
    private String name;
    private String email;
}