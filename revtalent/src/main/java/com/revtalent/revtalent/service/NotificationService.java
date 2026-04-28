package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Map<String, Object>> getNotifications() {
        return notificationRepository.findAll().stream()
                .map(n -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",        n.getId());
                    m.put("message",   n.getMessage());
                    m.put("type",      n.getType());
                    m.put("unread",    !n.isRead());
                    m.put("createdAt", n.getCreatedAt());
                    return m;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getUnreadNotifications() {
        return notificationRepository.findByReadFalse().stream()
                .map(n -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",        n.getId());
                    m.put("message",   n.getMessage());
                    m.put("type",      n.getType());
                    m.put("unread",    true);
                    m.put("createdAt", n.getCreatedAt());
                    return m;
                })
                .collect(Collectors.toList());
    }

    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void markAllAsRead() {
        List<Notification> all = notificationRepository.findByReadFalse();
        all.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(all);
    }
}