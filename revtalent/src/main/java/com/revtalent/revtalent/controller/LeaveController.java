package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.leave.LeaveRequestDTO;
import com.revtalent.revtalent.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin("*")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequestDTO req) {
        return ResponseEntity.ok(leaveService.apply(req));
    }

    @GetMapping
    public ResponseEntity<?> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAll());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approve(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.reject(id));
    }
}