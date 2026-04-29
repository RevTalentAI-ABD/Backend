package com.revtalent.revtalent.dto.notification;

import lombok.Data;

@Data
public class NotificationRequest {
    private String message;
    private String type;
}