package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ManagerController {

    private final ManagerService managerService;
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final NotificationService notificationService;


    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return managerService.getDashboard();
    }


    @GetMapping("/team")
    public List<Map<String, Object>> getTeam() {
        return employeeService.getTeam();
    }


    @GetMapping("/team/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    // 🔍 SEARCH
    @GetMapping("/team/search")
    public List<Map<String, Object>> searchTeam(@RequestParam String query) {
        return employeeService.searchTeam(query);
    }

    // 📅 ATTENDANCE
    @GetMapping("/attendance")
    public List<Attendance> getAttendance() {
        return attendanceService.getAttendance();
    }

    // 📊 ATTENDANCE SUMMARY
    @GetMapping("/attendance/summary")
    public List<Map<String, Object>> getSummary() {
        return attendanceService.getAttendanceSummary();
    }

    // 📥 EXPORT
    @GetMapping("/attendance/export")
    public String export() {
        return "Export success";
    }

    // 🏖️ LEAVES
    @GetMapping("/leaves")
    public List<LeaveRequest> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    @GetMapping("/leaves/pending")
    public List<LeaveRequest> getPending() {
        return leaveService.getPendingLeaves();
    }

    // ✅ APPROVE
    @PutMapping("/leaves/{id}/approve")
    public String approve(@PathVariable Long id) {
        leaveService.approveLeave(id);
        return "Approved";
    }

    // ❌ REJECT
    @PutMapping("/leaves/{id}/reject")
    public String reject(@PathVariable Long id) {
        leaveService.rejectLeave(id);
        return "Rejected";
    }

    // 📊 REPORTS
    @GetMapping("/reports/productivity")
    public List<Map<String, Object>> productivity() {
        return managerService.getProductivity();
    }

    @GetMapping("/reports/attendance")
    public String attendanceReport() {
        return "Attendance report";
    }

    @GetMapping("/reports/team-summary")
    public String teamSummary() {
        return "Team summary";
    }

    // 🔔 NOTIFICATIONS
    @GetMapping("/notifications")
    public List<Map<String, Object>> getNotifications() {
        return notificationService.getNotifications();
    }

    @PutMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id) {
        return "Marked read";
    }

    @PutMapping("/notifications/read-all")
    public String markAllRead() {
        return "All marked read";
    }

    // 📝 ACTIVITY
    @GetMapping("/activity")
    public List<Map<String, Object>> getActivity() {
        return managerService.getActivity();
    }

    @GetMapping("/profile")
    public String profile() {
        return "Manager profile";
    }
}