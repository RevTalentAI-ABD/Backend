package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.notification.NotificationResponse;
import com.revtalent.revtalent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        return ResponseEntity.ok(notificationService.getNotifications());
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Marked as read");
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok("All marked as read");
    }
}