package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping
    public ResponseEntity<List<LeaveRequestDTO>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPending() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        leaveService.approveLeave(id);
        return ResponseEntity.ok("Approved");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable Long id,
                                         @RequestParam(required = false) String reason) {
        leaveService.rejectLeave(id, reason);
        return ResponseEntity.ok("Rejected");
    }
}