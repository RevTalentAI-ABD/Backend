package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    int countByStatus(Attendance.Status status);
}