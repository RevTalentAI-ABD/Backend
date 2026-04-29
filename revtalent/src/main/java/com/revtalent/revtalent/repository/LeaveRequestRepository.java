package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    int countByStatus(LeaveRequest.Status status);
    List<LeaveRequest> findByStatus(LeaveRequest.Status status);
}