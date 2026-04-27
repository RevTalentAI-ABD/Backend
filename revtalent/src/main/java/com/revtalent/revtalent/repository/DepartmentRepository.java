package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}