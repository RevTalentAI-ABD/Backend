package com.revtalent.revtalent.service;

import com.revtalent.revtalent.config.JwtUtil;
import com.revtalent.revtalent.dto.auth.LoginRequest;
import com.revtalent.revtalent.dto.auth.RegisterRequest;
import com.revtalent.revtalent.dto.auth.UserResponse;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.DepartmentRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ── Login ──────────────────────────────────────────────────────────────────

    public Map<String, String> login(LoginRequest req) {
        User user = userRepo.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        res.put("role", user.getRole().name()); // "EMPLOYEE" / "MANAGER" / "HR_ADMIN"
        res.put("name", user.getName());
        return res;
    }

    // ── Register ───────────────────────────────────────────────────────────────

    @Transactional
    public UserResponse register(RegisterRequest req) {

        // Check duplicate
        userRepo.findByUsername(req.getUsername().toLowerCase())
                .ifPresent(u -> { throw new RuntimeException("Username already exists"); });

        // ── Build User ──────────────────────────────────────────────────────
        User user = new User();
        user.setName(req.getName());
        user.setUsername(req.getUsername().toLowerCase());
        user.setEmail(req.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setActive(true);

        // Role mapping:  "hradmin" → "HR_ADMIN", "employee" → "EMPLOYEE"
        String roleStr = req.getRole()
                .toUpperCase()
                .trim()
                .replace("HRADMIN", "HR_ADMIN")
                .replace("HR ADMIN", "HR_ADMIN");
        user.setRole(User.Role.valueOf(roleStr));

        // ── Build Employee (cascades User save) ─────────────────────────────
        Employee emp = new Employee();
        emp.setUser(user);
        emp.setStatus(Employee.Status.ACTIVE);
        emp.setJoiningDate(LocalDate.now());

        // Link department if provided
        if (req.getDepartment() != null && !req.getDepartment().isBlank()) {
            departmentRepo.findByName(req.getDepartment())
                    .ifPresent(emp::setDepartment);
        }

        // One save — saves both User + Employee
        Employee saved = employeeRepo.save(emp);
        User savedUser = saved.getUser();

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }
}