package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRepository;
    public Map<String, Object> getDashboard() {

        int teamSize = (int) employeeRepository.count();

        int present = attendanceRepository.countByStatus(Attendance.Status.PRESENT);
        int wfh = attendanceRepository.countByStatus(Attendance.Status.WFH);
        int absent = attendanceRepository.countByStatus(Attendance.Status.ABSENT);
        int onLeave = attendanceRepository.countByStatus(Attendance.Status.ON_LEAVE);

        int pendingLeaves = leaveRepository.countByStatus(LeaveRequest.Status.APPLIED);

        Map<String, Object> data = new HashMap<>();
        data.put("teamSize", teamSize);
        data.put("present", present);
        data.put("wfh", wfh);
        data.put("absent", absent);
        data.put("onLeave", onLeave);
        data.put("pendingLeaves", pendingLeaves);

        return data;
    }
    public List<Map<String, Object>> getProductivity() {
        return List.of(
                Map.of("month", "Jan", "score", 80),
                Map.of("month", "Feb", "score", 85),
                Map.of("month", "Mar", "score", 90)
        );
    }
    public List<Map<String, Object>> getActivity() {
        return List.of(
                Map.of("icon", "🟢", "text", "User logged in", "time", "2 min ago"),
                Map.of("icon", "📄", "text", "Leave applied", "time", "10 min ago")
        );
    }
}