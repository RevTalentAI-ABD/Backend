package com.revtalent.revtalent.dto;

import lombok.*;

@Data
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
}