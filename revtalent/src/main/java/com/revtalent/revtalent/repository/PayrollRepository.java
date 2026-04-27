package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
}