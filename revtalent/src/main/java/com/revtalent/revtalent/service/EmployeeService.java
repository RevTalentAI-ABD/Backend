package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.employee.EmployeeResponse;
import com.revtalent.revtalent.dto.employee.ManagerProfileResponse;
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

    public List<EmployeeResponse> getTeam() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EmployeeResponse toResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getId())
                .name(emp.getUser() != null ? emp.getUser().getUsername() : "N/A")
                .designation(emp.getDesignation())
                .department(emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A")
                .status(emp.getStatus().name())
                .employeeCode(emp.getEmployeeCode())
                .phone(emp.getPhone())
                .build();
    }

    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id)));
    }


    public List<EmployeeResponse> searchTeam(String query) {
        String q = query.toLowerCase();
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getUser() != null &&
                        emp.getUser().getUsername() != null &&
                        emp.getUser().getUsername().toLowerCase().contains(q))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ManagerProfileResponse getManagerProfile(Long managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        int teamSize = (int) employeeRepository.countByManagerId(managerId);

        return ManagerProfileResponse.builder()
                .id(manager.getId())
                .name(manager.getUser() != null ? manager.getUser().getName() : "N/A")
                .username(manager.getUser() != null ? manager.getUser().getUsername() : "N/A")
                .email(manager.getUser() != null ? manager.getUser().getEmail() : "N/A")
                .designation(manager.getDesignation())
                .department(manager.getDepartment() != null ? manager.getDepartment().getName() : "N/A")
                .employeeCode(manager.getEmployeeCode())
                .phone(manager.getPhone())
                .gender(manager.getGender())
                .joiningDate(manager.getJoiningDate())
                .dateOfBirth(manager.getDateOfBirth())
                .address(manager.getAddress())
                .profilePictureUrl(manager.getProfilePictureUrl())
                .status(manager.getStatus().name())
                .teamSize(teamSize)
                .build();
    }
}