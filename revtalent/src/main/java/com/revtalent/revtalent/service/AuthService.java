package com.revtalent.revtalent.service;

import com.revtalent.revtalent.config.JwtUtil;
import com.revtalent.revtalent.dto.auth.LoginRequest;
import com.revtalent.revtalent.dto.auth.RegisterRequest;
import com.revtalent.revtalent.dto.auth.UserResponse;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.Users;
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
        Users user = userRepo.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        res.put("role", user.getRole().name());
        res.put("name", user.getName());
        return res;
    }

    // ── Register ───────────────────────────────────────────────────────────────

    @Transactional
    public UserResponse register(RegisterRequest req) {

        userRepo.findByUsername(req.getUsername().toLowerCase())
                .ifPresent(u -> { throw new RuntimeException("Username already exists"); });

        Users user = new Users();
        user.setName(req.getName());
        user.setUsername(req.getUsername().toLowerCase());
        user.setEmail(req.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setActive(true);

        String roleStr = req.getRole()
                .toUpperCase()
                .trim()
                .replace("HRADMIN", "HR_ADMIN")
                .replace("HR ADMIN", "HR_ADMIN");
        user.setRole(Users.Role.valueOf(roleStr));

        Employee emp = new Employee();
        emp.setUser(user);
        emp.setStatus(Employee.Status.ACTIVE);
        emp.setJoiningDate(LocalDate.now());

        if (req.getDepartment() != null && !req.getDepartment().isBlank()) {
            departmentRepo.findByName(req.getDepartment())
                    .ifPresent(emp::setDepartment);
        }

        Employee saved = employeeRepo.save(emp);
        Users savedUser = saved.getUser();

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    // ── Verify Email ───────────────────────────────────────────────────────────  ✅ NEW

    public void verifyEmail(String email) {
        userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));
    }

    // ── Reset Password ─────────────────────────────────────────────────────────  ✅ NEW

    @Transactional
    public void resetPassword(String email, String newPassword) {
        Users user = userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}