package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    int countByStatus(Attendance.Status status);
    int countByStatusAndWorkDate(Attendance.Status status, LocalDate workDate);
    List<Attendance> findByWorkDate(LocalDate workDate);
}