package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.Payroll;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByEmployee_IdOrderByPayYearDescPayMonthDesc(Long empId);

    Optional<Payroll> findByEmployee_IdAndPayMonthAndPayYear(Long empId, int month, int year);

    List<Payroll> findByPayMonthAndPayYear(int month, int year);

    List<Payroll> findByEmployee_IdAndStatus(Long empId, Payroll.Status status);

    @Query("SELECT p FROM Payroll p WHERE p.payYear = :year AND p.status = :status")
    List<Payroll> findByYearAndStatus(@Param("year") int year, @Param("status") Payroll.Status status);

    @EntityGraph(attributePaths = {
            "employee",
            "employee.user",
            "employee.department"
    })
    List<Payroll> findAll();

    List<Payroll> findByEmployee_Id(Long employeeId);
    List<Payroll> findByEmployee(Employee emp);
}