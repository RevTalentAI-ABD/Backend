package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.LeaveRequest.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    // ✔ correct
    List<LeaveBalance> findByEmployee_Id(Long empId);

    // ✔ FIXED HERE
    Optional<LeaveBalance> findByEmployee_IdAndLeaveType(Long empId, LeaveType leaveType);
}