package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveBalanceRepository;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository; // ✅ properly injected

    // Get leave history
    public List<LeaveRequest> getLeaveHistory(Long empId) {
        employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));
        return leaveRequestRepository.findByEmployee_Id(empId);
    }

    // Apply leave
    public LeaveRequest applyLeave(LeaveApplyDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

        // Validate dates
        if (dto.getToDate().isBefore(dto.getFromDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date cannot be before start date");
        }

        // Validate leave type
        LeaveRequest.LeaveType leaveType;
        try {
            leaveType = LeaveRequest.LeaveType.valueOf(dto.getLeaveType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid leave type: " + dto.getLeaveType());
        }

        // Check for overlapping leave
        List<LeaveRequest> existing = leaveRequestRepository.findByEmployee_Id(employee.getId());
        boolean hasOverlap = existing.stream()
                .filter(l -> l.getStatus() != LeaveRequest.Status.CANCELLED
                        && l.getStatus() != LeaveRequest.Status.REJECTED)
                .anyMatch(l ->
                        !dto.getFromDate().isAfter(l.getEndDate()) &&
                                !dto.getToDate().isBefore(l.getStartDate()));

        if (hasOverlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Leave already applied for overlapping dates");
        }

        // Calculate total days
        long days = ChronoUnit.DAYS.between(dto.getFromDate(), dto.getToDate()) + 1;

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);
        leave.setStartDate(dto.getFromDate());
        leave.setEndDate(dto.getToDate());
        leave.setReason(dto.getReason());
        leave.setTotalDays(BigDecimal.valueOf(days));
        leave.setStatus(LeaveRequest.Status.APPLIED);

        return leaveRequestRepository.save(leave);
    }

    // Get leave by ID
    public LeaveRequest getLeaveById(Long leaveId) {
        return leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + leaveId));
    }

    // Cancel leave
    public void cancelLeave(Long leaveId) {
        LeaveRequest leave = getLeaveById(leaveId);

        if (leave.getStatus() != LeaveRequest.Status.APPLIED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only APPLIED leaves can be cancelled");
        }

        leave.setStatus(LeaveRequest.Status.CANCELLED);
        leaveRequestRepository.save(leave);
    }

    // Update leave status (HR only)
    public LeaveRequest updateLeaveStatus(Long leaveId, String status) {
        LeaveRequest leave = getLeaveById(leaveId);

        LeaveRequest.Status newStatus;
        try {
            newStatus = LeaveRequest.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status: " + status);
        }

        leave.setStatus(newStatus);
        return leaveRequestRepository.save(leave);
    }

    // Get leave balance — ✅ fixed (was crashing with NPE)
    public List<LeaveBalance> getLeaveBalance(Long empId) {
        employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));
        return leaveBalanceRepository.findByEmployee_Id(empId);
    }
}