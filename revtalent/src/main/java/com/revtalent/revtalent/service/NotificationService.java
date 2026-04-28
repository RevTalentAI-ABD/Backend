package com.revtalent.revtalent.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    public List<Map<String, Object>> getNotifications() {
        return List.of(
                Map.of("id", 1, "text", "Leave request pending", "unread", true),
                Map.of("id", 2, "text", "New employee joined", "unread", false)
        );
    }
}