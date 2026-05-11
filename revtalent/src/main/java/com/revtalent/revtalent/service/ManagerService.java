package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Attendance;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.LeaveRequest;
import com.revtalent.revtalent.repository.AttendanceRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRepository;

    // ── Dashboard ─────────────────────────────────────────────────────────────

    public Map<String, Object> getDashboard(String username) {
        Employee manager = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        LocalDate today = LocalDate.now();
        List<Long> teamIds = employeeRepository.findByManager_Id(manager.getId())
                .stream().map(Employee::getId).collect(Collectors.toList());

        // ✅ Fetch once, reuse — was called 4 separate times before
        List<Attendance> todayAttendance = attendanceRepository.findByWorkDate(today);

        int teamSize      = teamIds.size();
        int present       = (int) todayAttendance.stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId())
                        && a.getStatus() == Attendance.Status.PRESENT).count();
        int wfh           = (int) todayAttendance.stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId())
                        && a.getStatus() == Attendance.Status.WFH).count();
        int absent        = (int) todayAttendance.stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId())
                        && a.getStatus() == Attendance.Status.ABSENT).count();
        int onLeave       = (int) todayAttendance.stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId())
                        && a.getStatus() == Attendance.Status.ON_LEAVE).count();
        int pendingLeaves = (int) leaveRepository.findByEmployee_Manager_IdAndStatus(
                manager.getId(), LeaveRequest.Status.APPLIED).size();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("teamSize",      teamSize);
        data.put("presentCount",  present);   // ✅ fixed key names
        data.put("wfhCount",      wfh);
        data.put("absentCount",   absent);
        data.put("onLeaveCount",  onLeave);
        data.put("pendingLeaves", pendingLeaves);
        return data;
    }

    // ── Activity Feed ─────────────────────────────────────────────────────────

    public List<Map<String, Object>> getActivity() {
        List<Map<String, Object>> activities = new ArrayList<>();

        // ✅ Place 1 — leave applied — getName()
        leaveRepository.findAll().stream()
                .filter(l -> l.getAppliedAt() != null &&
                        l.getAppliedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .sorted(Comparator.comparing(LeaveRequest::getAppliedAt).reversed())
                .limit(5)
                .forEach(l -> {
                    String empName = l.getEmployee() != null && l.getEmployee().getUser() != null
                            ? l.getEmployee().getUser().getName() : "Unknown";  // ✅ getName()
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("icon", "📄");
                    activity.put("text", empName + " applied for " + l.getLeaveType().name().toLowerCase() + " leave");
                    activity.put("time", formatTimeAgo(l.getAppliedAt()));
                    activity.put("type", "LEAVE");
                    activities.add(activity);
                });

        // ✅ Place 2 — leave actioned — getName()
        leaveRepository.findAll().stream()
                .filter(l -> l.getActionedAt() != null &&
                        l.getActionedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .sorted(Comparator.comparing(LeaveRequest::getActionedAt).reversed())
                .limit(3)
                .forEach(l -> {
                    String empName = l.getEmployee() != null && l.getEmployee().getUser() != null
                            ? l.getEmployee().getUser().getName() : "Unknown";  // ✅ getName()
                    String icon       = l.getStatus() == LeaveRequest.Status.APPROVED ? "✅" : "❌";
                    String actionText = l.getStatus() == LeaveRequest.Status.APPROVED ? "approved" : "rejected";
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("icon", icon);
                    activity.put("text", "Leave " + actionText + " for " + empName);
                    activity.put("time", formatTimeAgo(l.getActionedAt()));
                    activity.put("type", "LEAVE_ACTION");
                    activities.add(activity);
                });

        // ✅ Place 3 — attendance check-in — getName()
        attendanceRepository.findByWorkDate(LocalDate.now()).stream()
                .filter(a -> a.getCheckIn() != null)
                .sorted(Comparator.comparing(Attendance::getCheckIn).reversed())
                .limit(3)
                .forEach(a -> {
                    String empName = a.getEmployee() != null && a.getEmployee().getUser() != null
                            ? a.getEmployee().getUser().getName() : "Unknown";  // ✅ getName()
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("icon", "🟢");
                    activity.put("text", empName + " checked in");
                    activity.put("time", formatTimeAgo(a.getCheckIn()));
                    activity.put("type", "ATTENDANCE");
                    activities.add(activity);
                });

        // Sort by most recent first
        activities.sort((a, b) -> b.get("time").toString().compareTo(a.get("time").toString()));

        return activities;
    }

    // ── Productivity ──────────────────────────────────────────────────────────

    public List<Map<String, Object>> getProductivity() {
        return attendanceRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getWorkDate().getMonth().getDisplayName(
                                java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    List<Attendance> monthData = entry.getValue();
                    long present = monthData.stream()
                            .filter(a -> a.getStatus() == Attendance.Status.PRESENT
                                    || a.getStatus() == Attendance.Status.WFH)
                            .count();
                    long total = monthData.size();
                    int score = total > 0 ? (int) ((present * 100) / total) : 0;

                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("month",   entry.getKey());
                    m.put("score",   score);
                    m.put("present", present);
                    m.put("total",   total);
                    return m;
                })
                .collect(Collectors.toList());
    }

    // ── Team Summary ──────────────────────────────────────────────────────────

    public Map<String, Object> getTeamSummary() {
        long totalEmployees  = employeeRepository.count();
        long activeEmployees = employeeRepository.countByStatus(Employee.Status.ACTIVE);
        long onLeave         = employeeRepository.countByStatus(Employee.Status.ON_LEAVE);
        long inactive        = employeeRepository.countByStatus(Employee.Status.INACTIVE);
        long pendingLeaves   = leaveRepository.countByStatus(LeaveRequest.Status.APPLIED);
        long approvedLeaves  = leaveRepository.countByStatus(LeaveRequest.Status.APPROVED);

        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepository.countByStatusAndWorkDate(Attendance.Status.PRESENT, today);
        long wfhToday     = attendanceRepository.countByStatusAndWorkDate(Attendance.Status.WFH, today);
        long absentToday  = attendanceRepository.countByStatusAndWorkDate(Attendance.Status.ABSENT, today);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalEmployees",  totalEmployees);
        summary.put("activeEmployees", activeEmployees);
        summary.put("onLeave",         onLeave);
        summary.put("inactive",        inactive);
        summary.put("pendingLeaves",   pendingLeaves);
        summary.put("approvedLeaves",  approvedLeaves);
        summary.put("presentToday",    presentToday);
        summary.put("wfhToday",        wfhToday);
        summary.put("absentToday",     absentToday);
        return summary;
    }

    // ── Attendance Report ─────────────────────────────────────────────────────

    public Map<String, Object> getAttendanceReport() {
        LocalDate today        = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        // ✅ Fetch once, filter in memory
        List<Attendance> allRecords   = attendanceRepository.findAll();
        List<Attendance> thisMonth    = allRecords.stream()
                .filter(a -> !a.getWorkDate().isBefore(startOfMonth) && !a.getWorkDate().isAfter(today))
                .collect(Collectors.toList());
        List<Attendance> todayRecords = allRecords.stream()
                .filter(a -> a.getWorkDate().equals(today))
                .collect(Collectors.toList());

        long monthPresent = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
        long monthWfh     = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count();
        long monthAbsent  = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
        long monthOnLeave = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count();
        long monthTotal   = thisMonth.size();
        int  monthRate    = monthTotal > 0 ? (int) (((monthPresent + monthWfh) * 100) / monthTotal) : 0;

        long todayPresent = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
        long todayWfh     = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count();
        long todayAbsent  = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
        long todayOnLeave = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count();

        List<Map<String, Object>> perEmployee = allRecords.stream()
                .filter(a -> !a.getWorkDate().isBefore(startOfMonth))
                .collect(Collectors.groupingBy(a -> a.getEmployee().getId()))
                .entrySet().stream()
                .map(entry -> {
                    List<Attendance> empRecords = entry.getValue();
                    // ✅ getName() instead of getUsername()
                    String empName = empRecords.get(0).getEmployee().getUser() != null
                            ? empRecords.get(0).getEmployee().getUser().getName() : "N/A";
                    long present = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
                    long wfh     = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count();
                    long absent  = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
                    long onLeave = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count();
                    int  rate    = empRecords.size() > 0 ? (int) (((present + wfh) * 100) / empRecords.size()) : 0;

                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("employeeId",     entry.getKey());
                    m.put("employeeName",   empName);
                    m.put("present",        present);
                    m.put("wfh",            wfh);
                    m.put("absent",         absent);
                    m.put("onLeave",        onLeave);
                    m.put("attendanceRate", rate + "%");
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportMonth", startOfMonth.getMonth().toString() + " " + today.getYear());
        report.put("today", Map.of(
                "present",  todayPresent,
                "wfh",      todayWfh,
                "absent",   todayAbsent,
                "onLeave",  todayOnLeave
        ));
        report.put("monthSummary", Map.of(
                "present",        monthPresent,
                "wfh",            monthWfh,
                "absent",         monthAbsent,
                "onLeave",        monthOnLeave,
                "attendanceRate", monthRate + "%"
        ));
        report.put("perEmployee", perEmployee);
        return report;
    }

    // ── PDF Report ────────────────────────────────────────────────────────────

    public byte[] generateReportsPdf() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Manager Report"));
            document.add(new Paragraph("Generated on: " + LocalDate.now()));
            document.add(new Paragraph(" "));

            Map<String, Object> summary = getTeamSummary();
            document.add(new Paragraph("=== Team Summary ==="));
            for (Map.Entry<String, Object> entry : summary.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
            }
            document.add(new Paragraph(" "));

            Map<String, Object> attendance = getAttendanceReport();
            document.add(new Paragraph("=== Attendance Report ==="));
            document.add(new Paragraph("Month: " + attendance.get("reportMonth")));
            Map<String, Object> monthSummary = (Map<String, Object>) attendance.get("monthSummary");
            for (Map.Entry<String, Object> entry : monthSummary.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String formatTimeAgo(LocalDateTime dateTime) {
        long minutes = java.time.Duration.between(dateTime, LocalDateTime.now()).toMinutes();
        if (minutes < 1)  return "just now";
        if (minutes < 60) return minutes + " min ago";
        long hours = minutes / 60;
        if (hours < 24)   return hours + " hr ago";
        long days = hours / 24;
        return days + " day" + (days > 1 ? "s" : "") + " ago";
    }
}