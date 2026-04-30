package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.dto.LeaveBalanceDTO;
import com.revtalent.revtalent.dto.LeaveHistoryDTO;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveBalanceRepository;
import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    //private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRepository;

    private LeaveRequest fetchLeaveEntity(Long leaveId) {
        return leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found: " + leaveId));
    }

    public List<LeaveBalanceDTO> getLeaveBalance(Long empId) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployee_Id(empId);
        return balances.stream()
                .map(b -> new LeaveBalanceDTO(
                        b.getLeaveType().name(),
                        b.getUsedDays().intValue(),
                        b.getTotalDays().intValue()
                ))
                .collect(Collectors.toList());
    }

    public List<LeaveHistoryDTO> getLeaveHistory(Long empId) {
        return leaveRepository.findByEmployee_Id(empId)
                .stream()
                .map(LeaveHistoryDTO::from)
                .collect(Collectors.toList());
    }

    public LeaveHistoryDTO applyLeave(LeaveApplyDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found: " + dto.getEmployeeId()));

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(LeaveRequest.LeaveType.valueOf(dto.getLeaveType().toUpperCase()));
        leave.setStartDate(dto.getFromDate());
        leave.setEndDate(dto.getToDate());
        leave.setReason(dto.getReason());

        long days = ChronoUnit.DAYS.between(dto.getFromDate(), dto.getToDate()) + 1;
        leave.setTotalDays(BigDecimal.valueOf(days));
        leave.setStatus(LeaveRequest.Status.APPLIED);

        return LeaveHistoryDTO.from(leaveRepository.save(leave));
    }

    public LeaveHistoryDTO getLeaveById(Long leaveId) {
        return LeaveHistoryDTO.from(fetchLeaveEntity(leaveId));
    }

    public void cancelLeave(Long leaveId) {
        LeaveRequest leave = fetchLeaveEntity(leaveId);
        if (leave.getStatus() == LeaveRequest.Status.CANCELLED) {
            throw new RuntimeException("Leave is already cancelled");
        }
        leave.setStatus(LeaveRequest.Status.CANCELLED);
        leaveRepository.save(leave);
    }

    public LeaveHistoryDTO updateLeaveStatus(Long leaveId, String status) {
        LeaveRequest leave = fetchLeaveEntity(leaveId);
        try {
            leave.setStatus(LeaveRequest.Status.valueOf(status.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
        return LeaveHistoryDTO.from(leaveRepository.save(leave));
    }
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