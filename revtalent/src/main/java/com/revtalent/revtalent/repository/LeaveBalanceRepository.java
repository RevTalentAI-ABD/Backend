package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
}