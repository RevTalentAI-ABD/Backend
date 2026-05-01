package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByUser_Username(String username);

    Optional<Employee> findByUser_Email(String email);

    long countByStatus(Employee.Status status);

    long countByManagerId(Long managerId);

    List<Employee> findByUser_NameContainingIgnoreCase(String name);

    List<Employee> findByDepartment_Name(String name);

    List<Employee> findByStatus(Employee.Status status);
}