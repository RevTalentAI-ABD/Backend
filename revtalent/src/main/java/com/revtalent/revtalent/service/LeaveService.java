package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.dto.LeaveBalanceDTO;
import com.revtalent.revtalent.dto.LeaveHistoryDTO;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveBalanceRepository;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;

    private LeaveRequest fetchLeaveEntity(Long leaveId) {
        return leaveRequestRepository.findById(leaveId)
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
        return leaveRequestRepository.findByEmployee_Id(empId)
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

        return LeaveHistoryDTO.from(leaveRequestRepository.save(leave));
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
        leaveRequestRepository.save(leave);
    }

    public LeaveHistoryDTO updateLeaveStatus(Long leaveId, String status) {
        LeaveRequest leave = fetchLeaveEntity(leaveId);
        try {
            leave.setStatus(LeaveRequest.Status.valueOf(status.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
        return LeaveHistoryDTO.from(leaveRequestRepository.save(leave));
    }
}