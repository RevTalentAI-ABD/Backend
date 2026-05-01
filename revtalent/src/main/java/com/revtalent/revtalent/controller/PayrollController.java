package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.PayrollDTO;
import com.revtalent.revtalent.dto.payroll.PayrollResponse;
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
@CrossOrigin("*")
public class PayrollController {

    private final PayrollService payrollService;

    // ── Employee endpoints ────────────────────────────────────────────────────

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<PayrollResponse>> getByEmployee(@PathVariable Long empId) {
        return ResponseEntity.ok(payrollService.getByEmployee(empId));
    }

    @GetMapping("/employee/{empId}/month")
    public ResponseEntity<PayrollResponse> getByMonth(
            @PathVariable Long empId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.getByEmployeeAndMonth(empId, month, year));
    }

    @GetMapping("/employee/{empId}/status")
    public ResponseEntity<List<PayrollResponse>> getByStatus(
            @PathVariable Long empId,
            @RequestParam Payroll.Status status) {
        return ResponseEntity.ok(payrollService.getByStatus(empId, status));
    }

    @PostMapping("/employee/{empId}")
    public ResponseEntity<PayrollResponse> create(
            @PathVariable Long empId,
            @RequestBody PayrollDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.create(empId, dto));
    }

    @PutMapping("/{payrollId}")
    public ResponseEntity<PayrollResponse> update(
            @PathVariable Long payrollId,
            @RequestBody PayrollDTO dto) {
        return ResponseEntity.ok(payrollService.update(payrollId, dto));
    }

    @PutMapping("/{payrollId}/process")
    public ResponseEntity<PayrollResponse> processSingle(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.process(payrollId));
    }

    @PutMapping("/{payrollId}/pay")
    public ResponseEntity<PayrollResponse> markPaid(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.markPaid(payrollId));
    }

    // ── Month endpoints ───────────────────────────────────────────────────────

    @GetMapping("/month")
    public ResponseEntity<List<PayrollResponse>> getAllByMonth(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.getByMonth(month, year));
    }

    @PutMapping("/bulk-process")
    public ResponseEntity<List<PayrollResponse>> bulkProcess(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.bulkProcess(month, year));
    }

    // ── HR / Manager endpoints ────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAll() {
        return ResponseEntity.ok(payrollService.getAll());
    }

    @PostMapping("/generate")
    public ResponseEntity<List<PayrollResponse>> generate(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.generatePayroll(month, year));
    }

    @PostMapping("/process-all")
    public ResponseEntity<List<PayrollResponse>> processAll() {
        return ResponseEntity.ok(payrollService.processPayroll());
    }
}