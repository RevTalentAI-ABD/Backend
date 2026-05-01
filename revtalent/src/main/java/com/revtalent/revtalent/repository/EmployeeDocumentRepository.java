package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.EmployeeDocument;
import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository




public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployee(Employee employee);
}