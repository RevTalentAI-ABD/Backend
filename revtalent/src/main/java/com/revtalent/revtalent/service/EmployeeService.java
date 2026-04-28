package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<Map<String, Object>> getTeam() {
        return employeeRepository.findAll().stream().map(emp -> {
            Map<String, Object> m = new HashMap<>();

            m.put("id", emp.getId());

            m.put("name",
                    emp.getUser() != null
                            ? emp.getUser().getUsername()
                            : "N/A"
            );

            m.put("role", emp.getDesignation());

            m.put("dept",
                    emp.getDepartment() != null
                            ? emp.getDepartment().getName()
                            : "N/A"
            );

            return m;
        }).collect(Collectors.toList());
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }


    public List<Map<String, Object>> searchTeam(String query) {
        String q = query.toLowerCase();
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getUser() != null &&
                        emp.getUser().getUsername() != null &&
                        emp.getUser().getUsername().toLowerCase().contains(q))
                .map(emp -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",   emp.getId());
                    m.put("name", emp.getUser().getUsername());
                    m.put("role", emp.getDesignation());
                    m.put("dept", emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A");
                    return m;
                })
                .collect(Collectors.toList());
    }
}