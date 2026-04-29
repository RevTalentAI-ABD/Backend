package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.notification.NotificationResponse;
import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnreadNotifications() {
        return notificationRepository.findByReadFalse().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void markAllAsRead() {
        List<Notification> all = notificationRepository.findByReadFalse();
        all.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(all);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType() != null ? n.getType().name() : null)
                .unread(!n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}