//package com.revtalent.revtalent.controller;
//
//import com.revtalent.revtalent.dto.LeaveApplyDTO;
//import com.revtalent.revtalent.service.LeaveService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/leaves")
//@RequiredArgsConstructor
//public class LeaveController {
//
//    private final LeaveService service;
//
//    @GetMapping("/balance/{empId}")
//    public Object getBalance(@PathVariable Long empId) {
//        return service.getLeaveBalance(empId);
//    }
//
//    @GetMapping("/history/{empId}")
//    public Object getHistory(@PathVariable Long empId) {
//        return service.getLeaveHistory(empId);
//    }
//
//    @PostMapping("/apply")
//    public Object apply(@RequestBody LeaveApplyDTO dto) {
//        return service.applyLeave(dto);
//    }
//
//    @GetMapping("/{leaveId}")
//    public Object getLeave(@PathVariable Long leaveId) {
//        return service.getLeaveById(leaveId);
//    }
//
//    @DeleteMapping("/{leaveId}/cancel")
//    public void cancel(@PathVariable Long leaveId) {
//        service.cancelLeave(leaveId);
//    }
//
//    @PutMapping("/{leaveId}/status")
//    public Object updateStatus(@PathVariable Long leaveId,
//                               @RequestBody String status) {
//        return service.updateLeaveStatus(leaveId, status);
//    }
//}

package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.LeaveApplyDTO;
import com.revtalent.revtalent.dto.LeaveHistoryDTO;
import com.revtalent.revtalent.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService service;

    @GetMapping("/balance/{empId}")
    public ResponseEntity<?> getBalance(@PathVariable Long empId) {
        return ResponseEntity.ok(service.getLeaveBalance(empId));
    }

    @GetMapping("/history/{empId}")
    public ResponseEntity<List<LeaveHistoryDTO>> getHistory(@PathVariable Long empId) {
        return ResponseEntity.ok(service.getLeaveHistory(empId));
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody LeaveApplyDTO dto) {
        return ResponseEntity.ok(service.applyLeave(dto));
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveHistoryDTO> getLeave(@PathVariable Long leaveId) {
        return ResponseEntity.ok(service.getLeaveById(leaveId));
    }

    @DeleteMapping("/{leaveId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long leaveId) {
        service.cancelLeave(leaveId);
        return ResponseEntity.noContent().build();
    }

}