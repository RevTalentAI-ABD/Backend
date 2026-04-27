package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {
}