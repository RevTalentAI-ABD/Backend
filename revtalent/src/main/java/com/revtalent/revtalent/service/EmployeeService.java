package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.CreateEmployeeRequest;
import com.revtalent.revtalent.model.Department;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.DepartmentRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee createEmployee(CreateEmployeeRequest request) {

        // Build User
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // @PrePersist copies to passwordHash
        if (request.getRole() != null) {
            user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        }

        // Build Employee
        Employee emp = new Employee();
        emp.setUser(user);
        emp.setEmployeeCode(request.getEmployeeCode());
        emp.setDesignation(request.getDesignation());
        emp.setJoiningDate(request.getJoiningDate());
        emp.setPhone(request.getPhone());
        emp.setAddress(request.getAddress());

        // Attach department if provided
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
            emp.setDepartment(dept);
        }

        return employeeRepository.save(emp);
    }

    public Employee updateEmployee(Long id, Employee updated) {
        Employee emp = getEmployeeById(id);

        emp.setEmployeeCode(updated.getEmployeeCode());
        emp.setDesignation(updated.getDesignation());
        emp.setPhone(updated.getPhone());
        emp.setAddress(updated.getAddress());

        if (updated.getDepartment() != null) {
            emp.setDepartment(updated.getDepartment());
        }
        if (updated.getManager() != null) {
            emp.setManager(updated.getManager());
        }
        if (updated.getStatus() != null) {
            emp.setStatus(updated.getStatus());
        }

        return employeeRepository.save(emp);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public Object getDashboardStats(Long id) {
        return "Dashboard stats for employee " + id;
    }

    public Object getSchedule(Long id) {
        return "Schedule for employee " + id;
    }

    public Object getAnnouncements() {
        return "Company announcements";
    }
}