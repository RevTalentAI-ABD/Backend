package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.payroll.PayrollResponse;
import com.revtalent.revtalent.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService service;

    @PostMapping("/generate")
    public ResponseEntity<List<PayrollResponse>> generate(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(service.generatePayroll(month, year));
    }

    @PostMapping("/process")
    public ResponseEntity<List<PayrollResponse>> process() {
        return ResponseEntity.ok(service.processPayroll());
    }

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<PayrollResponse>> getByEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByEmployee(id));
    }
}