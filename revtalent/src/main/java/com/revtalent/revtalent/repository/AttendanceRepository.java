package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEmployee_IdOrderByWorkDateDesc(Long empId);

    List<Attendance> findByEmployee_IdAndWorkDateBetweenOrderByWorkDateDesc(
            Long empId, LocalDate from, LocalDate to);

    Optional<Attendance> findByEmployee_IdAndWorkDate(Long empId, LocalDate workDate);

    List<Attendance> findByWorkDate(LocalDate workDate);

    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :empId " +
            "AND a.workDate BETWEEN :from AND :to AND a.status = :status")
    List<Attendance> findByEmpIdAndDateRangeAndStatus(
            @Param("empId") Long empId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") Attendance.Status status);

    int countByEmployee_IdAndWorkDateBetweenAndStatus(
            Long empId, LocalDate from, LocalDate to, Attendance.Status status);
}