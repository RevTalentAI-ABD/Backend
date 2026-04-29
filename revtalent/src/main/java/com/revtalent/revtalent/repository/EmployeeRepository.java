package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByUser_NameContainingIgnoreCase(String name);

    List<Employee> findByDepartment_Name(String name);
    List<Employee> findByStatus(Employee.Status status);
}