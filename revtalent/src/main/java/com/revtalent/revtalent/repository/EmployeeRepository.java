package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}