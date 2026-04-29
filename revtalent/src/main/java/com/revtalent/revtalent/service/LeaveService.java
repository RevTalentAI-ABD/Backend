package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRepository;

    public List<LeaveRequestDTO> getAllLeaves() {
        return leaveRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    private LeaveRequestDTO toDTO(LeaveRequest leave) {
        return LeaveRequestDTO.builder()
                .id(leave.getId())
                .employeeName(leave.getEmployee() != null && leave.getEmployee().getUser() != null
                        ? leave.getEmployee().getUser().getUsername()
                        : "N/A")
                .leaveType(leave.getLeaveType().name())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .status(leave.getStatus().name())
                .reason(leave.getReason())
                .rejectionReason(leave.getRejectionReason())
                .build();
    }


    public List<LeaveRequestDTO> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveRequest.Status.APPLIED).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void rejectLeave(Long id, String rejectionReason) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        if (leave.getStatus() != LeaveRequest.Status.APPLIED) {
            throw new RuntimeException("Only pending leaves can be rejected");
        }
        leave.setStatus(LeaveRequest.Status.REJECTED);
        leave.setRejectionReason(rejectionReason);
        leave.setActionedAt(LocalDateTime.now());
        leaveRepository.save(leave);
    }

    public void approveLeave(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
        if (leave.getStatus() != LeaveRequest.Status.APPLIED) {
            throw new RuntimeException("Only pending leaves can be approved");
        }
        leave.setStatus(LeaveRequest.Status.APPROVED);
        leave.setActionedAt(LocalDateTime.now());
        leaveRepository.save(leave);
    }
}