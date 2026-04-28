package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public List<Notification> getNotifications(Long empId) {
        return notificationRepository.findByEmployee_IdOrderByCreatedAtDesc(empId);
    }


    public List<Notification> getUnreadNotifications(Long empId) {
        return notificationRepository.findByEmployee_IdAndReadFalse(empId);
    }


    public int getUnreadCount(Long empId) {
        return notificationRepository.countByEmployee_IdAndReadFalse(empId);
    }


    public Notification markAsRead(Long notifId) {
        Notification notification = notificationRepository.findById(notifId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notifId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }


    public void markAllAsRead(Long empId) {
        notificationRepository.markAllAsReadByEmployeeId(empId);
    }


    public void deleteNotification(Long notifId) {
        if (!notificationRepository.existsById(notifId)) {
            throw new RuntimeException("Notification not found with id: " + notifId);
        }
        notificationRepository.deleteById(notifId);
    }
}