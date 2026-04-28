package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<Map<String, Object>> getNotifications() {
        return notificationService.getNotifications();
    }

    @GetMapping("/unread")
    public List<Map<String, Object>> getUnread() {
        return notificationService.getUnreadNotifications();
    }

    @PutMapping("/{id}/read")
    public String markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "Marked as read";
    }

    @PutMapping("/read-all")
    public String markAllRead() {
        notificationService.markAllAsRead();
        return "All marked as read";
    }
}