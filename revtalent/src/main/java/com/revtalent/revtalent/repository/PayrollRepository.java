package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.Payroll;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    // ✅ Override findAll() with EntityGraph
    @EntityGraph(attributePaths = {
            "employee",
            "employee.user",
            "employee.department"
    })
    List<Payroll> findAll();

    List<Payroll> findByEmployee_Id(Long employeeId);
    List<Payroll> findByEmployee(Employee emp);
}