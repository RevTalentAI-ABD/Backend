package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByUser_Username(String username);

    Optional<Employee> findByUser_Email(String email);
}