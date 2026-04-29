package com.revtalent.revtalent.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private String role;
}