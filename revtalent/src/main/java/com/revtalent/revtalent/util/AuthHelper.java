package com.revtalent.revtalent.util;

import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final EmployeeRepository employeeRepository;

    // Get logged-in employee
    public Employee getLoggedInEmployee(Authentication auth) {
        return employeeRepository.findByUser_Username(auth.getName())
                .orElseThrow(() -> new RuntimeException("Employee not found for user: " + auth.getName()));
    }

    // Check if logged-in user is HR_ADMIN
    public boolean isHR(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR_ADMIN"));
    }

    // Validate access — employee can only access own data, HR can access all
    public void validateAccess(Authentication auth, Long requestedEmpId) {
        if (isHR(auth)) return; // HR can access anything

        Employee loggedIn = getLoggedInEmployee(auth);
        if (!loggedIn.getId().equals(requestedEmpId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only access your own data");
        }
    }
}