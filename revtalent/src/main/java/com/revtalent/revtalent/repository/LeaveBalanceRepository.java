package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.LeaveRequest.LeaveType;
import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    // ✔ correct
    List<LeaveBalance> findByEmployee_Id(Long empId);

    // ✔ FIXED HERE
    Optional<LeaveBalance> findByEmployee_IdAndLeaveType(Long empId, LeaveType leaveType);

    Optional<LeaveBalance> findByEmployee(Employee employee);
    Optional<LeaveBalance> findByEmployeeAndYear(Employee employee, Integer year);
    Optional<LeaveBalance> findByEmployee_IdAndLeaveTypeAndYear(Long empId, LeaveType leaveType, Integer year);
}