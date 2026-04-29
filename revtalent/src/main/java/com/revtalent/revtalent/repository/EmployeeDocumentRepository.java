package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.EmployeeDocument;
import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployee(Employee employee);
}