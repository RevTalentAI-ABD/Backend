package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService service;

    @GetMapping("/balance/{empId}")
    public Object getBalance(@PathVariable Long empId) {
        return service.getLeaveBalance(empId);
    }

    @GetMapping("/history/{empId}")
    public Object getHistory(@PathVariable Long empId) {
        return service.getLeaveHistory(empId);
    }

    @PostMapping("/apply")
    public Object apply(@RequestBody LeaveApplyDTO dto) {
        return service.applyLeave(dto);
    }

    @GetMapping("/{leaveId}")
    public Object getLeave(@PathVariable Long leaveId) {
        return service.getLeaveById(leaveId);
    }

    @DeleteMapping("/{leaveId}/cancel")
    public void cancel(@PathVariable Long leaveId) {
        service.cancelLeave(leaveId);
    }

    @PutMapping("/{leaveId}/status")
    public Object updateStatus(@PathVariable Long leaveId,
                               @RequestBody String status) {
        return service.updateLeaveStatus(leaveId, status);
    }
}