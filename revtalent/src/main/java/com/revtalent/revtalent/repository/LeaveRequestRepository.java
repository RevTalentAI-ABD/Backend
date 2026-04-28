package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.LeaveRequest.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // All leaves of an employee
    List<LeaveRequest> findByEmployee_Id(Long empId);

    // Filter by status
    List<LeaveRequest> findByEmployee_IdAndStatus(Long empId, Status status);
}