package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.dto.leave.LeaveResponse;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository repo;

    public LeaveResponse apply(LeaveRequest leave) {
        leave.setStatus(LeaveRequest.Status.APPLIED);
        LeaveRequest saved = repo.save(leave);
        return mapToDTO(saved);
    }

    public List<LeaveResponse> getAll() {
        return repo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public LeaveResponse approve(Long id) {
        LeaveRequest leave = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveRequest.Status.APPROVED);
        leave.setActionedAt(LocalDateTime.now());

        return mapToDTO(repo.save(leave));
    }

    public LeaveResponse reject(Long id) {
        LeaveRequest leave = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveRequest.Status.REJECTED);
        leave.setActionedAt(LocalDateTime.now());

        return mapToDTO(repo.save(leave));
    }


    private LeaveResponse mapToDTO(LeaveRequest l) {
        return LeaveResponse.builder()
                .id(l.getId())
                .leaveType(l.getLeaveType().name())
                .status(l.getStatus().name())
                .reason(l.getReason())
                .startDate(l.getStartDate())
                .endDate(l.getEndDate())
                .build();
    }
    @Autowired
    private EmployeeRepository employeeRepo;

    public LeaveResponse apply(LeaveRequestDTO req) {

        Employee emp = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(emp);
        leave.setLeaveType(LeaveRequest.LeaveType.valueOf(req.getLeaveType()));
        leave.setStartDate(req.getStartDate());
        leave.setEndDate(req.getEndDate());
        leave.setReason(req.getReason());
        leave.setStatus(LeaveRequest.Status.APPLIED);

        return mapToDTO(repo.save(leave));
    }
}