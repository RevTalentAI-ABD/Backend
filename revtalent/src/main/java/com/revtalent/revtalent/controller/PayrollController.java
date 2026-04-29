//package com.revtalent.revtalent.controller;
//
//import com.revtalent.revtalent.dto.PayrollDTO;
//import com.revtalent.revtalent.model.Payroll;
//import com.revtalent.revtalent.service.PayrollService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/payroll")
//@RequiredArgsConstructor
//public class PayrollController {
//
//    private final PayrollService payrollService;
//
//    // GET all payrolls for employee
//    @GetMapping("/employee/{empId}")
//    public List<Payroll> getByEmployee(@PathVariable Long empId) {
//        return payrollService.getByEmployee(empId);
//    }
//
//    // GET payroll for specific month/year
//    @GetMapping("/employee/{empId}/month")
//    public Payroll getByMonth(
//            @PathVariable Long empId,
//            @RequestParam int month,
//            @RequestParam int year) {
//        return payrollService.getByEmployeeAndMonth(empId, month, year);
//    }
//
//    // GET all payrolls for a month (HR view)
//    @GetMapping("/month")
//    public List<Payroll> getAllByMonth(
//            @RequestParam int month,
//            @RequestParam int year) {
//        return payrollService.getByMonth(month, year);
//    }
//
//    // GET by status
//    @GetMapping("/employee/{empId}/status")
//    public List<Payroll> getByStatus(
//            @PathVariable Long empId,
//            @RequestParam Payroll.Status status) {
//        return payrollService.getByStatus(empId, status);
//    }
//
//    // POST create payroll
//    @PostMapping("/employee/{empId}")
//    public Payroll create(
//            @PathVariable Long empId,
//            @RequestBody PayrollDTO dto) {
//        return payrollService.create(empId, dto);
//    }
//
//    // PUT update payroll
//    @PutMapping("/{payrollId}")
//    public Payroll update(
//            @PathVariable Long payrollId,
//            @RequestBody PayrollDTO dto) {
//        return payrollService.update(payrollId, dto);
//    }
//
//    // PUT process payroll
//    @PutMapping("/{payrollId}/process")
//    public Payroll process(@PathVariable Long payrollId) {
//        return payrollService.process(payrollId);
//    }
//
//    // PUT mark as paid
//    @PutMapping("/{payrollId}/pay")
//    public Payroll markPaid(@PathVariable Long payrollId) {
//        return payrollService.markPaid(payrollId);
//    }
//
//    // PUT bulk process for a month
//    @PutMapping("/bulk-process")
//    public List<Payroll> bulkProcess(
//            @RequestParam int month,
//            @RequestParam int year) {
//        return payrollService.bulkProcess(month, year);
//    }
//
//    // DELETE payroll
//    @DeleteMapping("/{payrollId}")
//    public void delete(@PathVariable Long payrollId) {
//        payrollService.delete(payrollId);
//    }
//}


package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.PayrollDTO;
import com.revtalent.revtalent.dto.PayrollResponseDTO;
import com.revtalent.revtalent.model.Payroll;
import com.revtalent.revtalent.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<PayrollResponseDTO>> getByEmployee(@PathVariable Long empId) {
        return ResponseEntity.ok(payrollService.getByEmployee(empId));
    }

    @GetMapping("/employee/{empId}/month")
    public ResponseEntity<PayrollResponseDTO> getByMonth(
            @PathVariable Long empId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.getByEmployeeAndMonth(empId, month, year));
    }

    @GetMapping("/month")
    public ResponseEntity<List<PayrollResponseDTO>> getAllByMonth(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.getByMonth(month, year));
    }

    @GetMapping("/employee/{empId}/status")
    public ResponseEntity<List<PayrollResponseDTO>> getByStatus(
            @PathVariable Long empId,
            @RequestParam Payroll.Status status) {
        return ResponseEntity.ok(payrollService.getByStatus(empId, status));
    }

    @PostMapping("/employee/{empId}")
    public ResponseEntity<PayrollResponseDTO> create(
            @PathVariable Long empId,
            @RequestBody PayrollDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.create(empId, dto));
    }

    @PutMapping("/{payrollId}")
    public ResponseEntity<PayrollResponseDTO> update(
            @PathVariable Long payrollId,
            @RequestBody PayrollDTO dto) {
        return ResponseEntity.ok(payrollService.update(payrollId, dto));
    }

    @PutMapping("/{payrollId}/process")
    public ResponseEntity<PayrollResponseDTO> process(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.process(payrollId));
    }

    @PutMapping("/{payrollId}/pay")
    public ResponseEntity<PayrollResponseDTO> markPaid(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.markPaid(payrollId));
    }

    @PutMapping("/bulk-process")
    public ResponseEntity<List<PayrollResponseDTO>> bulkProcess(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.bulkProcess(month, year));
    }

    @DeleteMapping("/{payrollId}")
    public ResponseEntity<Void> delete(@PathVariable Long payrollId) {
        payrollService.delete(payrollId);
        return ResponseEntity.noContent().build();
    }
}