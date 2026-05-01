package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.NotificationResponseDTO;
import com.revtalent.revtalent.dto.notification.NotificationResponse;
import com.revtalent.revtalent.dto.notification.NotificationRequest;
import com.revtalent.revtalent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;

    // ── Employee endpoints (empId scoped) ────────────────────────────────────

    @GetMapping("/{empId}")
    public ResponseEntity<List<NotificationResponseDTO>> getByEmployee(@PathVariable Long empId) {
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
    public ResponseEntity<Void> markAllForEmployee(@PathVariable Long empId) {
        notificationService.markAllAsRead(empId);
        return ResponseEntity.noContent().build();
    }

    // ── Manager / Global endpoints ────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getAllUnread() {
        return ResponseEntity.ok(notificationService.getAllUnreadNotifications());
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllReadGlobal() {
        notificationService.markAllAsReadGlobal();
        return ResponseEntity.ok("All marked as read");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NotificationRequest dto) {
        return ResponseEntity.ok(notificationService.create(dto));
    }
}