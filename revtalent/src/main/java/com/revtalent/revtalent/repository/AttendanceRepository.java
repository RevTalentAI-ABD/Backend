package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeAndWorkDate(Employee employee, LocalDate workDate);

    @EntityGraph(attributePaths = {
            "employee",
            "employee.user",
            "employee.department"
    })
    List<Attendance> findAll();

    List<Attendance> findByEmployee(Employee employee);

    List<Attendance> findByWorkDate(LocalDate date);

    // ✅ For summary - count by status and date range
    long countByStatusAndWorkDateBetween(Attendance.Status status, LocalDate from, LocalDate to);

    // ✅ For summary - count by attendance type and date range
    long countByAttendanceTypeAndWorkDateBetween(Attendance.AttendanceType type, LocalDate from, LocalDate to);

    // ✅ For summary - date range fetch
    List<Attendance> findByWorkDateBetween(LocalDate from, LocalDate to);
}