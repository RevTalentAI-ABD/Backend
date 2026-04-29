package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {
}