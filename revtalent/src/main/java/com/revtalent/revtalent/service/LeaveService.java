package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRepository;

    public List<LeaveRequest> getAllLeaves() {
        return leaveRepository.findAll();
    }

    public List<LeaveRequest> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveRequest.Status.APPLIED);
    }

    public void approveLeave(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveRequest.Status.APPROVED);
        leaveRepository.save(leave);
    }

    public void rejectLeave(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveRequest.Status.REJECTED);
        leaveRepository.save(leave);
    }
}