package com.revtalent.revtalent.service;

import com.revtalent.revtalent.config.JwtUtil;
import com.revtalent.revtalent.dto.auth.LoginRequest;
import com.revtalent.revtalent.dto.auth.RegisterRequest;
import com.revtalent.revtalent.dto.auth.UserResponse;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveBalance;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.Users;
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
    private final OtpService otpService;


    public Map<String, String> login(LoginRequest req) {

        // Try username first, then fall back to email
        Users users = userRepo.findByUsername(req.getUsername().toLowerCase())
                .or(() -> userRepo.findByEmail(req.getUsername().toLowerCase()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), users.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(users.getUsername(), users.getRole().name());

        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        res.put("role",  users.getRole().name());
        res.put("name",  users.getName());
        res.put("email", users.getEmail());

        return res;
    }

    // ── Verify OTP → only email verification (used after Register) ────────────
    public Map<String, String> verifyOtp(String email, String otp) {
        boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            throw new RuntimeException("Invalid OTP");
        }

        Map<String, String> res = new HashMap<>();
        res.put("message", "Email verified successfully. Please login.");
        return res;
    }

    // ── Register ──────────────────────────────────────────────────────────────
    @Transactional
    public UserResponse register(RegisterRequest req) {

        userRepo.findByUsername(req.getUsername().toLowerCase())
                .ifPresent(u -> { throw new RuntimeException("Username already exists"); });

        // Parse role
        String roleStr = req.getRole()
                .toUpperCase()
                .trim()
                .replace("HRADMIN", "HR_ADMIN")
                .replace("HR ADMIN", "HR_ADMIN");
        Users.Role role = Users.Role.valueOf(roleStr);

        // Build and save User
        Users users = new Users();
        users.setName(req.getName());
        users.setUsername(req.getUsername().toLowerCase());
        users.setEmail(req.getEmail().toLowerCase());
        users.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        users.setRole(role);
        users.setActive(true);

        // ── Candidates: save User only, no Employee record needed ─────────────
        if (role == Users.Role.CANDIDATE) {
            Users savedUsers = userRepo.save(users);
            return UserResponse.builder()
                    .id(savedUsers.getId())
                    .name(savedUsers.getName())
                    .username(savedUsers.getUsername())
                    .email(savedUsers.getEmail())
                    .role(savedUsers.getRole().name())
                    .build();
        }

        // ── Employees / Managers / HR: create Employee record + leave balances ─
        Employee emp = new Employee();
        emp.setUser(users);
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
                createBalance(saved, LeaveRequest.LeaveType.ANNUAL,  15)
        );
        leaveBalanceRepository.saveAll(balances);

        Users savedUsers = saved.getUser();
        return UserResponse.builder()
                .id(savedUsers.getId())
                .name(savedUsers.getName())
                .username(savedUsers.getUsername())
                .email(savedUsers.getEmail())
                .role(savedUsers.getRole().name())
                .build();
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private LeaveBalance createBalance(Employee emp, LeaveRequest.LeaveType type, int total) {
        LeaveBalance lb = new LeaveBalance();
        lb.setEmployee(emp);
        lb.setLeaveType(type);
        lb.setTotalDays(BigDecimal.valueOf(total));
        lb.setUsedDays(BigDecimal.ZERO);
        lb.setYear(LocalDate.now().getYear());
        return lb;
    }

    // ── Verify Email (Forgot Password flow) ───────────────────────────────────
    public void verifyEmail(String email) {
        userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));
    }

    // ── Reset Password ────────────────────────────────────────────────────────
    @Transactional
    public void resetPassword(String email, String newPassword) {
        Users users = userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }

        users.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(users);
    }
}