package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/{empId}")
    public List<Notification> getAll(@PathVariable Long empId) {
        return notificationService.getNotifications(empId);
    }


    @GetMapping("/{empId}/unread")
    public List<Notification> getUnread(@PathVariable Long empId) {
        return notificationService.getUnreadNotifications(empId);
    }


    @GetMapping("/{empId}/unread-count")
    public int getUnreadCount(@PathVariable Long empId) {
        return notificationService.getUnreadCount(empId);
    }


    @PutMapping("/{notifId}/read")
    public Notification markRead(@PathVariable Long notifId) {
        return notificationService.markAsRead(notifId);
    }


    @PutMapping("/{empId}/read-all")
    public void markAll(@PathVariable Long empId) {
        notificationService.markAllAsRead(empId);
    }


    @DeleteMapping("/{notifId}")
    public void delete(@PathVariable Long notifId) {
        notificationService.deleteNotification(notifId);
    }
}