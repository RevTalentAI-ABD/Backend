package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Employee queries
    List<Attendance> findByEmployee_IdOrderByWorkDateDesc(Long empId);

    List<Attendance> findByEmployee_IdAndWorkDateBetweenOrderByWorkDateDesc(
            Long empId, LocalDate from, LocalDate to);

    Optional<Attendance> findByEmployee_IdAndWorkDate(Long empId, LocalDate workDate);

    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :empId " +
            "AND a.workDate BETWEEN :from AND :to AND a.status = :status")
    List<Attendance> findByEmpIdAndDateRangeAndStatus(
            @Param("empId") Long empId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") Attendance.Status status);

    int countByEmployee_IdAndWorkDateBetweenAndStatus(
            Long empId, LocalDate from, LocalDate to, Attendance.Status status);

    // Manager / shared queries
    List<Attendance> findByWorkDate(LocalDate workDate);

    int countByStatus(Attendance.Status status);

    int countByStatusAndWorkDate(Attendance.Status status, LocalDate workDate);
    Optional<Attendance> findByEmployeeAndWorkDate(Employee employee, LocalDate workDate);

    @EntityGraph(attributePaths = {
            "employee",
            "employee.user",
            "employee.department"
    })
    List<Attendance> findAll();

    List<Attendance> findByEmployee(Employee employee);


    // ✅ For summary - count by status and date range
    long countByStatusAndWorkDateBetween(Attendance.Status status, LocalDate from, LocalDate to);

    // ✅ For summary - count by attendance type and date range
    long countByAttendanceTypeAndWorkDateBetween(Attendance.AttendanceType type, LocalDate from, LocalDate to);

    // ✅ For summary - date range fetch
    List<Attendance> findByWorkDateBetween(LocalDate from, LocalDate to);




        long countByStatus(String status);

        long countByWorkDateBetween(LocalDate start, LocalDate end);

        long countByStatusAndWorkDateBetween(String status, LocalDate start, LocalDate end);


    long countByEmployeeAndWorkDateBetween(Employee employee, LocalDate start, LocalDate end);

    long countByEmployeeAndStatusAndWorkDateBetween(
            Employee employee,
            Attendance.Status status,
            LocalDate start,
            LocalDate end
    );
}