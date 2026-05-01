package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.auth.LoginRequest;
import com.revtalent.revtalent.dto.auth.RegisterRequest;
import com.revtalent.revtalent.dto.auth.UserResponse;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;

    public Map<String, String> login(LoginRequest req) {

        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPasswordHash().equals(req.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        Map<String, String> res = new HashMap<>();
        res.put("token", "login-success"); // later replace with JWT
        return res;
    }

    public UserResponse register(RegisterRequest req) {
        userRepo.findByUsername(req.getUsername())
                .ifPresent(u -> {
                    throw new RuntimeException("Username already exists");
                });
        User user = new User();
        user.setName(req.getName());
        user.setUsername(req.getUsername().toLowerCase());
        user.setEmail(req.getEmail().toLowerCase());
        user.setPasswordHash(req.getPassword()); // (later encrypt)
        user.setRole(User.Role.valueOf(req.getRole().toUpperCase()));
        user.setActive(true);

        User saved = userRepo.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .build();
    }
}