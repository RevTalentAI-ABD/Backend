package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveBalanceRepository;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    // 🔹 Get leave history
    public List<LeaveRequest> getLeaveHistory(Long empId) {
        return leaveRequestRepository.findByEmployee_Id(empId);
    }

    // 🔹 Apply Leave (IMPORTANT LOGIC FIXED)
    public LeaveRequest applyLeave(LeaveApplyDTO dto) {

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveRequest leave = new LeaveRequest();

        leave.setEmployee(employee);

        // Convert string → enum
        leave.setLeaveType(
                LeaveRequest.LeaveType.valueOf(dto.getLeaveType().toUpperCase())
        );

        leave.setStartDate(dto.getFromDate());
        leave.setEndDate(dto.getToDate());
        leave.setReason(dto.getReason());

        // Calculate total days
        long days = ChronoUnit.DAYS.between(dto.getFromDate(), dto.getToDate()) + 1;
        leave.setTotalDays(BigDecimal.valueOf(days));

        leave.setStatus(LeaveRequest.Status.APPLIED);

        return leaveRequestRepository.save(leave);
    }


    public LeaveRequest getLeaveById(Long leaveId) {
        return leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
    }


    public void cancelLeave(Long leaveId) {
        LeaveRequest leave = getLeaveById(leaveId);

        leave.setStatus(LeaveRequest.Status.CANCELLED);
        leaveRequestRepository.save(leave);
    }


    public LeaveRequest updateLeaveStatus(Long leaveId, String status) {

        LeaveRequest leave = getLeaveById(leaveId);

        leave.setStatus(
                LeaveRequest.Status.valueOf(status.toUpperCase())
        );

        return leaveRequestRepository.save(leave);
    }
    public Object getLeaveBalance(Long empId) {
        LeaveBalanceRepository leaveBalanceRepository = null;
        return leaveBalanceRepository.findByEmployee_Id(empId);
    }
}