package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.LeaveRequest.Status;
import com.revtalent.revtalent.model.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // All leaves of an employee
    List<LeaveRequest> findByEmployee_Id(Long empId);

    // Filter by status
    List<LeaveRequest> findByEmployee_IdAndStatus(Long empId, Status status);
    int countByStatus(LeaveRequest.Status status);
    List<LeaveRequest> findByEmployee(Employee employee);
    List<LeaveRequest> findByStatus(LeaveRequest.Status status);
    List<LeaveRequest> findByEmployee_Manager_Id(Long managerId);
    List<LeaveRequest> findByEmployee_Manager_IdAndStatus(Long managerId, LeaveRequest.Status status);

}