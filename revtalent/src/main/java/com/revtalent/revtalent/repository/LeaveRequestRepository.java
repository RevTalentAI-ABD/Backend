package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(Employee employee);

    List<LeaveRequest> findByStatus(LeaveRequest.Status status);
}