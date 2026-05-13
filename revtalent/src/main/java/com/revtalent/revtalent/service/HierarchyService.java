package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.Users;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HierarchyService {

    private final UserRepository usersRepository;
    private final EmployeeRepository employeeRepository;

    private static final String[] COLORS = {
            "#7C3AED", "#06B6D4", "#10B981", "#EC4899",
            "#F59E0B", "#8B5CF6", "#14B8A6", "#EF4444",
            "#3B82F6", "#F97316"
    };

    // GET /api/hierarchy/managers
    public List<Map<String, Object>> getManagersWithTeams() {
        List<Users> managers = usersRepository.findByRoleIn(
                List.of(Users.Role.MANAGER, Users.Role.HR_ADMIN)
        );

        List<Map<String, Object>> result = new ArrayList<>();

        for (Users manager : managers) {
            List<Users> team = usersRepository.findByManager(manager);

            List<Map<String, Object>> employeeDtos = team.stream()
                    .map(this::toDto)
                    .toList();

            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id",         manager.getId());
            dto.put("name",       manager.getName());
            dto.put("role",       friendlyRole(manager.getRole()));
            dto.put("department", manager.getDepartmentName());
            dto.put("avatar",     initials(manager.getName()));
            dto.put("color",      colorFor(manager.getId()));
            dto.put("employees",  employeeDtos);
            result.add(dto);
        }

        return result;
    }

    // GET /api/hierarchy/unassigned
    public List<Map<String, Object>> getUnassignedEmployees() {
        List<Users> unassigned = usersRepository.findByRoleAndManagerIsNull(Users.Role.EMPLOYEE);

        return unassigned.stream()
                .map(this::toDto)
                .toList();
    }

    // POST /api/hierarchy/assign
    public void assignEmployeesToManager(Long managerId, List<Long> employeeIds) {
        Users managerUser = usersRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId));

        List<Users> employees = usersRepository.findAllById(employeeIds);
        for (Users empUser : employees) {
            empUser.setManager(managerUser);
            
            // Sync with Employee entity
            Employee employeeEntity = empUser.getEmployee();
            Employee managerEntity = managerUser.getEmployee();
            if (employeeEntity != null) {
                employeeEntity.setManager(managerEntity);
                employeeRepository.save(employeeEntity);
            }
        }
        usersRepository.saveAll(employees);
    }

    // Helpers
    private Map<String, Object> toDto(Users u) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id",         u.getId());
        dto.put("name",       u.getName());
        dto.put("role",       friendlyRole(u.getRole()));
        dto.put("department", u.getDepartmentName());
        dto.put("email",      u.getEmail());
        dto.put("avatar",     initials(u.getName()));
        dto.put("color",      colorFor(u.getId()));
        return dto;
    }

    private String friendlyRole(Users.Role role) {
        if (role == null) return "Employee";
        return switch (role) {
            case MANAGER   -> "Manager";
            case HR_ADMIN  -> "HR Admin";
            case EMPLOYEE  -> "Employee";
            case CANDIDATE -> "Candidate";
        };
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) { if (!p.isEmpty()) sb.append(p.charAt(0)); }
        return sb.toString().toUpperCase().substring(0, Math.min(2, sb.length()));
    }

    private String colorFor(Long id) {
        return COLORS[(int)(id % COLORS.length)];
    }
}