package com.revtalent.revtalent.service;

import com.revtalent.revtalent.config.JwtUtil;
import com.revtalent.revtalent.dto.auth.LoginRequest;
import com.revtalent.revtalent.dto.auth.RegisterRequest;
import com.revtalent.revtalent.dto.auth.UserResponse;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.DepartmentRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveBalanceRepository;
import com.revtalent.revtalent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LeaveBalanceRepository leaveBalanceRepository;

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
        res.put("role", user.getRole().name());
        res.put("name", user.getName());
        return res;
    }

    // ── Register ───────────────────────────────────────────────────────────────

    @Transactional
    public UserResponse register(RegisterRequest req) {

        userRepo.findByUsername(req.getUsername().toLowerCase())
                .ifPresent(u -> { throw new RuntimeException("Username already exists"); });

        User user = new User();
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
        user.setRole(User.Role.valueOf(roleStr));

        Employee emp = new Employee();
        emp.setUser(user);
        emp.setStatus(Employee.Status.ACTIVE);
        emp.setJoiningDate(LocalDate.now());

        if (req.getDepartment() != null && !req.getDepartment().isBlank()) {
            departmentRepo.findByName(req.getDepartment())
                    .ifPresent(emp::setDepartment);
        }


        Employee saved = employeeRepo.save(emp);


        List<LeaveBalance> balances = List.of(
                createBalance(saved, LeaveRequest.LeaveType.CASUAL, 12),
                createBalance(saved, LeaveRequest.LeaveType.SICK,    8),
                createBalance(saved, LeaveRequest.LeaveType.ANNUAL, 15)
        );
        leaveBalanceRepository.saveAll(balances);

        User savedUser = saved.getUser();

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private LeaveBalance createBalance(Employee emp, LeaveRequest.LeaveType type, int total) {
        LeaveBalance lb = new LeaveBalance();
        lb.setEmployee(emp);
        lb.setLeaveType(type);
        lb.setTotalDays(BigDecimal.valueOf(total));
        lb.setUsedDays(BigDecimal.ZERO);
        lb.setYear(java.time.LocalDate.now().getYear());
        return lb;
    }

    // ── Verify Email ───────────────────────────────────────────────────────────

    public void verifyEmail(String email) {
        userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));
    }

    // ── Reset Password ─────────────────────────────────────────────────────────

    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}