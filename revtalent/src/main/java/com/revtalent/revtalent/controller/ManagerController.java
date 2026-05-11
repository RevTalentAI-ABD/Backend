//package com.revtalent.revtalent.controller;
//
//import com.revtalent.revtalent.model.User;
//import com.revtalent.revtalent.model.Employee;
//import com.revtalent.revtalent.repository.UserRepository;
//import com.revtalent.revtalent.repository.EmployeeRepository;
//import com.revtalent.revtalent.service.DashboardService;
//import java.security.Principal;
//
//import org.springframework.web.bind.annotation.*;
//import org.springframework.http.ResponseEntity;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/manager")
//@CrossOrigin("*")
//public class ManagerController {
//
//    @Autowired private DashboardService dashboardService;
//    @Autowired private UserRepository userRepo;
//    @Autowired private EmployeeRepository employeeRepo;
//
//    @GetMapping("/dashboard-summary")
//    public ResponseEntity<?> getDashboard() {
//        return ResponseEntity.ok(dashboardService.getSummary());
//    }
//
//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfile(Principal principal) {
//        String email = principal.getName();
//        User user = userRepo.findByEmail(email).orElseThrow();
//        Employee emp = employeeRepo.findByUser_Email(email).orElseThrow();
//
//        Map<String, Object> profile = new HashMap<>();
//        profile.put("id", emp.getId());
//        profile.put("name", emp.getName());
//        profile.put("email", emp.getEmail());
//        profile.put("designation", emp.getDesignation());
//        profile.put("departmentName", emp.getDepartment() != null ? emp.getDepartment().getName() : null); // ✅ fixed
//        profile.put("role", user.getRole());
//        return ResponseEntity.ok(profile);
//    }
//}

package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.DashboardService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin("*")
public class ManagerController {

    @Autowired private DashboardService dashboardService;

    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    // ✅ profile endpoint REMOVED — it already exists in DashboardController
}