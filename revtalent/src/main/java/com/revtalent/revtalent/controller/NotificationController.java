package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.notification.NotificationRequest;
import com.revtalent.revtalent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NotificationRequest dto) {
        return ResponseEntity.ok(service.create(dto));
    }
}