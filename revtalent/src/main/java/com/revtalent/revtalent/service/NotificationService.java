package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.NotificationResponseDTO;
import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotifications(Long empId) {
        return notificationRepository.findByEmployee_IdOrderByCreatedAtDesc(empId)
                .stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long empId) {
        return notificationRepository.findByEmployee_IdAndReadFalse(empId)
                .stream()
                .map(NotificationResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(Long empId) {
        return notificationRepository.countByEmployee_IdAndReadFalse(empId);
    }

    @Transactional
    public NotificationResponseDTO markAsRead(Long notifId) {
        Notification notification = notificationRepository.findById(notifId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notifId));
        notification.setRead(true);
        return NotificationResponseDTO.from(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(Long empId) {
        notificationRepository.markAllAsReadByEmployeeId(empId);
    }

    @Transactional
    public void deleteNotification(Long notifId) {
        if (!notificationRepository.existsById(notifId)) {
            throw new RuntimeException("Notification not found: " + notifId);
        }
        notificationRepository.deleteById(notifId);
    }
}