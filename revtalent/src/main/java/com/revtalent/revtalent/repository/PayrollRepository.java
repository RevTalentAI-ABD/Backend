package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
}