package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.NotificationResponseDTO;
import com.revtalent.revtalent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{empId}")
    public ResponseEntity<List<NotificationResponseDTO>> getAll(@PathVariable Long empId) {
        return ResponseEntity.ok(notificationService.getNotifications(empId));
    }

    @GetMapping("/{empId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnread(@PathVariable Long empId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(empId));
    }

    @GetMapping("/{empId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long empId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(empId));
    }

    @PutMapping("/{notifId}/read")
    public ResponseEntity<NotificationResponseDTO> markRead(@PathVariable Long notifId) {
        return ResponseEntity.ok(notificationService.markAsRead(notifId));
    }

    @PutMapping("/{empId}/read-all")
    public ResponseEntity<Void> markAll(@PathVariable Long empId) {
        notificationService.markAllAsRead(empId);
        return ResponseEntity.noContent().build();
    }
}