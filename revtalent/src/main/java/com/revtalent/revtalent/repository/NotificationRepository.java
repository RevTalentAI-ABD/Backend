package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReadFalse();

    List<Notification> findByType(Notification.Type type);
}