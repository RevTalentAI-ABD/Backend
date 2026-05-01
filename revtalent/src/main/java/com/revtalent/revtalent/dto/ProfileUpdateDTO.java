package com.revtalent.revtalent.dto;

import lombok.*;

@Data
public class ProfileUpdateDTO {
    private String name;
    private String email;
    private String phone;
    private String dept;
}