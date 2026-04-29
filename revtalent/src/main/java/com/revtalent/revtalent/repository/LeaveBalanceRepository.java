package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployee(Employee employee);
    Optional<LeaveBalance> findByEmployeeAndYear(Employee employee, Integer year);
}