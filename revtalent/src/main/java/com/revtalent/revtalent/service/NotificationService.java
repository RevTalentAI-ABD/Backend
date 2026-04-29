package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.notification.NotificationRequest;
import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;

    public List<Notification> getAll() { return repo.findAll(); }

    public Notification create(NotificationRequest dto) {
        Notification n = new Notification();
        n.setMessage(dto.getMessage());
        n.setType(Notification.Type.valueOf(dto.getType()));
        return repo.save(n);
    }
}