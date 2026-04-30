package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.CreateEmployeeRequest;
import com.revtalent.revtalent.dto.EmployeeResponse;
import com.revtalent.revtalent.dto.PatchEmployeeRequest;
import com.revtalent.revtalent.dto.UpdateEmployeeRequest;
import com.revtalent.revtalent.exception.ResourceNotFoundException;
import com.revtalent.revtalent.model.Department;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.DepartmentRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Read ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee emp = findOrThrow(id);
        return EmployeeResponse.from(emp);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeResponse::from)
                .collect(Collectors.toList());
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        employeeRepository.findByUser_Username(request.getUsername()).ifPresent(e -> {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        });
        employeeRepository.findByUser_Email(request.getEmail()).ifPresent(e -> {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        });

        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        } else {
            user.setRole(User.Role.EMPLOYEE);
        }

        Employee emp = new Employee();
        emp.setUser(user);
        emp.setEmployeeCode(request.getEmployeeCode());
        emp.setDesignation(request.getDesignation());
        emp.setJoiningDate(request.getJoiningDate());
        emp.setPhone(request.getPhone());
        emp.setAddress(request.getAddress());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
            emp.setDepartment(dept);
        }

        return EmployeeResponse.from(employeeRepository.save(emp));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee emp = findOrThrow(id);


        if (request.getDesignation() != null && !request.getDesignation().isBlank()) {
            emp.setDesignation(request.getDesignation());
        }
        if (request.getPhone() != null) {
            emp.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            emp.setAddress(request.getAddress());
        }
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
            emp.setDepartment(dept);
        }
        Employee.Status parsedStatus = request.parsedStatus();
        if (parsedStatus != null) {
            emp.setStatus(parsedStatus);
        }

        return EmployeeResponse.from(employeeRepository.save(emp));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteEmployee(Long id) {
        findOrThrow(id);
        employeeRepository.deleteById(id);
    }

    // ── Misc ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<String> getAnnouncements() {
        return List.of("Welcome to RevTalent!", "Q2 performance reviews begin next week.");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    @Transactional
    public EmployeeResponse patchPersonalInfo(Long id, PatchEmployeeRequest request) {
        Employee emp = findOrThrow(id);

        // Update employee fields
        if (request.getPhone() != null)            emp.setPhone(request.getPhone());
        if (request.getAddress() != null)          emp.setAddress(request.getAddress());
        if (request.getDesignation() != null)      emp.setDesignation(request.getDesignation());
        if (request.getProfilePictureUrl() != null) emp.setProfilePictureUrl(request.getProfilePictureUrl());

        // Update user fields
        if (emp.getUser() != null) {
            if (request.getName() != null)  emp.getUser().setName(request.getName());
            if (request.getEmail() != null) emp.getUser().setEmail(request.getEmail());
        }

        return EmployeeResponse.from(employeeRepository.save(emp));
    }
}