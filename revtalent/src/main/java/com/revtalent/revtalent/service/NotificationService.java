package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.NotificationResponseDTO;
import com.revtalent.revtalent.dto.notification.NotificationResponse;
import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ── Private helper ────────────────────────────────────────────────────────

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType() != null ? n.getType().name() : null)
                .unread(!n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    // ── Employee endpoints (empId scoped) ─────────────────────────────────────

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

    // ── Manager endpoints (all notifications, no empId scope) ─────────────────

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllUnreadNotifications() {
        return notificationRepository.findByReadFalse().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAllAsReadGlobal() {
        List<Notification> unread = notificationRepository.findByReadFalse();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}