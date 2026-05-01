package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.mongo.Policy;
import com.revtalent.revtalent.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService service;

    @PostMapping
    public Policy create(@RequestBody Policy policy) {
        return service.create(policy);
    }

    @GetMapping
    public List<Policy> getAll() {
        return service.getAll();
    }
}