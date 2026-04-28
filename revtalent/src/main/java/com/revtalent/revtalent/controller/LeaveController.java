package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping
    public List<LeaveRequest> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    @GetMapping("/pending")
    public List<LeaveRequest> getPending() {
        return leaveService.getPendingLeaves();
    }

    @PutMapping("/{id}/approve")
    public String approve(@PathVariable Long id) {
        leaveService.approveLeave(id);
        return "Approved";
    }

    @PutMapping("/{id}/reject")
    public String reject(@PathVariable Long id) {
        leaveService.rejectLeave(id);
        return "Rejected";
    }
}