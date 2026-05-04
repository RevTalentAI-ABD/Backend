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

    public Map<String, Object> getDashboard(String username) {
        Employee manager = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        LocalDate today = LocalDate.now();
        List<Long> teamIds = employeeRepository.findByManager_Id(manager.getId())
                .stream().map(Employee::getId).collect(Collectors.toList());

        int teamSize     = teamIds.size();
        int present      = (int) attendanceRepository.findByWorkDate(today).stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId()) && a.getStatus() == Attendance.Status.PRESENT).count();
        int wfh          = (int) attendanceRepository.findByWorkDate(today).stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId()) && a.getStatus() == Attendance.Status.WFH).count();
        int absent       = (int) attendanceRepository.findByWorkDate(today).stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId()) && a.getStatus() == Attendance.Status.ABSENT).count();
        int onLeave      = (int) attendanceRepository.findByWorkDate(today).stream()
                .filter(a -> teamIds.contains(a.getEmployee().getId()) && a.getStatus() == Attendance.Status.ON_LEAVE).count();
        int pendingLeaves = (int) leaveRepository.findByEmployee_Manager_IdAndStatus(
                manager.getId(), LeaveRequest.Status.APPLIED).size();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("teamSize", teamSize);
        data.put("present", present);
        data.put("wfh", wfh);
        data.put("absent", absent);
        data.put("onLeave", onLeave);
        data.put("pendingLeaves", pendingLeaves);
        return data;
    }
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
                    m.put("month", entry.getKey());
                    m.put("score", score);
                    m.put("present", present);
                    m.put("total", total);
                    return m;
                })
                .collect(Collectors.toList());
    }
    public List<Map<String, Object>> getActivity() {
        List<Map<String, Object>> activities = new ArrayList<>();

        leaveRepository.findAll().stream()
                .filter(l -> l.getAppliedAt() != null &&
                        l.getAppliedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .sorted(Comparator.comparing(LeaveRequest::getAppliedAt).reversed())
                .limit(5)
                .forEach(l -> {
                    String empName = l.getEmployee() != null && l.getEmployee().getUser() != null
                            ? l.getEmployee().getUser().getUsername() : "Unknown";
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("icon", "📄");
                    activity.put("text", empName + " applied for " + l.getLeaveType().name().toLowerCase() + " leave");
                    activity.put("time", formatTimeAgo(l.getAppliedAt()));
                    activity.put("type", "LEAVE");
                    activities.add(activity);
                });

        leaveRepository.findAll().stream()
                .filter(l -> l.getActionedAt() != null &&
                        l.getActionedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .sorted(Comparator.comparing(LeaveRequest::getActionedAt).reversed())
                .limit(3)
                .forEach(l -> {
                    String empName = l.getEmployee() != null && l.getEmployee().getUser() != null
                            ? l.getEmployee().getUser().getUsername() : "Unknown";
                    String action = l.getStatus() == LeaveRequest.Status.APPROVED ? "✅" : "❌";
                    String actionText = l.getStatus() == LeaveRequest.Status.APPROVED ? "approved" : "rejected";
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("icon", action);
                    activity.put("text", "Leave " + actionText + " for " + empName);
                    activity.put("time", formatTimeAgo(l.getActionedAt()));
                    activity.put("type", "LEAVE_ACTION");
                    activities.add(activity);
                });

        attendanceRepository.findByWorkDate(LocalDate.now()).stream()
                .filter(a -> a.getCheckIn() != null)
                .sorted(Comparator.comparing(Attendance::getCheckIn).reversed())
                .limit(3)
                .forEach(a -> {
                    String empName = a.getEmployee() != null && a.getEmployee().getUser() != null
                            ? a.getEmployee().getUser().getUsername() : "Unknown";
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("icon", "🟢");
                    activity.put("text", empName + " checked in");
                    activity.put("time", formatTimeAgo(a.getCheckIn()));
                    activity.put("type", "ATTENDANCE");
                    activities.add(activity);
                });

        activities.sort(Comparator.comparing(m -> m.get("time").toString()));

        return activities;
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        long minutes = java.time.Duration.between(dateTime, LocalDateTime.now()).toMinutes();
        if (minutes < 1)   return "just now";
        if (minutes < 60)  return minutes + " min ago";
        long hours = minutes / 60;
        if (hours < 24)    return hours + " hr ago";
        long days = hours / 24;
        return days + " day" + (days > 1 ? "s" : "") + " ago";
    }
    public Map<String, Object> getTeamSummary() {
        long totalEmployees  = employeeRepository.count();
        long activeEmployees = employeeRepository.countByStatus(Employee.Status.ACTIVE);
        long onLeave         = employeeRepository.countByStatus(Employee.Status.ON_LEAVE);
        long inactive        = employeeRepository.countByStatus(Employee.Status.INACTIVE);

        long pendingLeaves   = leaveRepository.countByStatus(LeaveRequest.Status.APPLIED);
        long approvedLeaves  = leaveRepository.countByStatus(LeaveRequest.Status.APPROVED);

        long presentToday    = attendanceRepository.countByStatusAndWorkDate(Attendance.Status.PRESENT, LocalDate.now());
        long wfhToday        = attendanceRepository.countByStatusAndWorkDate(Attendance.Status.WFH, LocalDate.now());
        long absentToday     = attendanceRepository.countByStatusAndWorkDate(Attendance.Status.ABSENT, LocalDate.now());

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

    public Map<String, Object> getAttendanceReport() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        List<Attendance> allRecords = attendanceRepository.findAll();
        List<Attendance> thisMonth = allRecords.stream()
                .filter(a -> !a.getWorkDate().isBefore(startOfMonth) && !a.getWorkDate().isAfter(today))
                .collect(Collectors.toList());

        List<Attendance> todayRecords = allRecords.stream()
                .filter(a -> a.getWorkDate().equals(today))
                .collect(Collectors.toList());
        long monthPresent  = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
        long monthWfh      = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count();
        long monthAbsent   = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
        long monthOnLeave  = thisMonth.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count();
        long monthTotal    = thisMonth.size();
        int  monthRate     = monthTotal > 0 ? (int) (((monthPresent + monthWfh) * 100) / monthTotal) : 0;

        long todayPresent  = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
        long todayWfh      = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count();
        long todayAbsent   = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
        long todayOnLeave  = todayRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count();
        List<Map<String, Object>> perEmployee = allRecords.stream()
                .filter(a -> !a.getWorkDate().isBefore(startOfMonth))
                .collect(Collectors.groupingBy(a -> a.getEmployee().getId()))
                .entrySet().stream()
                .map(entry -> {
                    List<Attendance> empRecords = entry.getValue();
                    String empName = empRecords.get(0).getEmployee().getUser() != null
                            ? empRecords.get(0).getEmployee().getUser().getUsername() : "N/A";
                    long present  = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
                    long wfh      = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.WFH).count();
                    long absent   = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
                    long onLeave  = empRecords.stream().filter(a -> a.getStatus() == Attendance.Status.ON_LEAVE).count();
                    int  rate     = empRecords.size() > 0 ? (int) (((present + wfh) * 100) / empRecords.size()) : 0;

                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("employeeId",       entry.getKey());
                    m.put("employeeName",     empName);
                    m.put("present",          present);
                    m.put("wfh",              wfh);
                    m.put("absent",           absent);
                    m.put("onLeave",          onLeave);
                    m.put("attendanceRate",   rate + "%");
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportMonth",       startOfMonth.getMonth().toString() + " " + today.getYear());
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

    public byte[] generateReportsPdf() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            // Title
            document.add(new Paragraph("Manager Report"));
            document.add(new Paragraph("Generated on: " + LocalDate.now()));
            document.add(new Paragraph(" "));

            // 🔹 Team Summary
            Map<String, Object> summary = getTeamSummary();
            document.add(new Paragraph("=== Team Summary ==="));
            for (Map.Entry<String, Object> entry : summary.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
            }

            document.add(new Paragraph(" "));

            // 🔹 Dashboard Data
            Map<String, Object> dashboard = getDashboard("system");
            document.add(new Paragraph("=== Dashboard ==="));
            for (Map.Entry<String, Object> entry : dashboard.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
            }

            document.add(new Paragraph(" "));

            // 🔹 Attendance Report (Month)
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
}