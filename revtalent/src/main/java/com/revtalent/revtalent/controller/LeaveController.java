package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.dto.LeaveHistoryDTO;
import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveController {

    private final LeaveService leaveService;

    // ── Employee endpoints ────────────────────────────────────────────────────

    @GetMapping("/balance/{empId}")
    public ResponseEntity<?> getBalance(@PathVariable Long empId) {
        return ResponseEntity.ok(leaveService.getLeaveBalance(empId));
    }

    @GetMapping("/history/{empId}")
    public ResponseEntity<List<LeaveHistoryDTO>> getHistory(@PathVariable Long empId) {
        return ResponseEntity.ok(leaveService.getLeaveHistory(empId));
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody LeaveApplyDTO dto) {
        return ResponseEntity.ok(leaveService.applyLeave(dto));
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveHistoryDTO> getLeave(@PathVariable Long leaveId) {
        return ResponseEntity.ok(leaveService.getLeaveById(leaveId));
    }

    @DeleteMapping("/{leaveId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long leaveId) {
        leaveService.cancelLeave(leaveId);
        return ResponseEntity.noContent().build();
    }

    // ── Manager endpoints ─────────────────────────────────────────────────────

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