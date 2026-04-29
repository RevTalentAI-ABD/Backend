package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.employee.EmployeeResponse;
import com.revtalent.revtalent.dto.employee.EmployeeUpdateRequest;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repo;

    public List<EmployeeResponse> getAll() {
        return repo.findByStatus(Employee.Status.ACTIVE)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public EmployeeResponse getById(Long id) {
        Employee e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToDTO(e);
    }

    public EmployeeResponse create(Employee emp) {
        return mapToDTO(repo.save(emp));
    }

    public EmployeeResponse update(Long id, EmployeeUpdateRequest req) {

        Employee emp = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (req.getDesignation() != null)
            emp.setDesignation(req.getDesignation());

        if (req.getPhone() != null)
            emp.setPhone(req.getPhone());

        if (req.getAddress() != null)
            emp.setAddress(req.getAddress());

        return mapToDTO(repo.save(emp));
    }

    public boolean delete(Long id) {
        Employee emp = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        emp.setStatus(Employee.Status.INACTIVE);
        repo.save(emp);

        return true;
    }


    private EmployeeResponse mapToDTO(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .employeeCode(e.getEmployeeCode())
                .designation(e.getDesignation())
                .status(e.getStatus().name())
                .departmentName(
                        e.getDepartment() != null ? e.getDepartment().getName() : null
                )
                .managerId(
                        e.getManager() != null ? e.getManager().getId() : null
                )
                .build();
    }
}