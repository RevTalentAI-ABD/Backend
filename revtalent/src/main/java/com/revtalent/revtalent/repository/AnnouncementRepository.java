package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Announcement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository
        extends JpaRepository<Announcement, Long> {

}