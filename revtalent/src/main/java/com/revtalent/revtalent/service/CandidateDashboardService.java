//package com.revtalent.revtalent.service;
//
//import com.revtalent.revtalent.model.Candidate;
//import com.revtalent.revtalent.model.User;
//import com.revtalent.revtalent.repository.CandidateRepository;
//import com.revtalent.revtalent.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CandidateDashboardService {
//
//    private final UserRepository userRepository;
//    private final CandidateRepository candidateRepository;
//
//    // ── Profile ───────────────────────────────────────────────────────────────
//    @Transactional(readOnly = true)
//    public Map<String, Object> getProfile(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//
//        return Map.of(
//                "id",        user.getId(),
//                "name",      user.getName() != null ? user.getName() : "",
//                "email",     user.getEmail(),
//                "username",  user.getUsername(),
//                "role",      user.getRole().name(),
//                "firstName", firstName(user.getName()),
//                "lastName",  lastName(user.getName())
//        );
//    }
//
//    // ── Applications — find all Candidate rows matching this user's email ─────
//    @Transactional(readOnly = true)
//    public List<Map<String, Object>> getApplications(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//
//        List<Candidate> candidates = candidateRepository.findByEmail(user.getEmail());
//
//        return candidates.stream().map(c -> {
//            Map<String, Object> m = new java.util.LinkedHashMap<>();
//            m.put("id",          c.getId());
//            m.put("jobId",       c.getJobPosting() != null ? c.getJobPosting().getId() : null);
//            m.put("jobTitle",    c.getJobPosting() != null ? c.getJobPosting().getTitle() : "");
//            m.put("department",  c.getJobPosting() != null && c.getJobPosting().getDepartment() != null
//                    ? c.getJobPosting().getDepartment().getName() : "");
//            m.put("status",      c.getStatus().name());
//            m.put("appliedDate", c.getAppliedAt());
//            m.put("phone",       c.getPhone());
//            return m;
//        }).collect(Collectors.toList());
//    }
//
//    // ── Upcoming interviews — Candidate rows where status = INTERVIEW ─────────
//    @Transactional(readOnly = true)
//    public List<Map<String, Object>> getUpcomingInterviews(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//
//        List<Candidate> interviews = candidateRepository.findByEmail(user.getEmail())
//                .stream()
//                .filter(c -> c.getStatus() == Candidate.Status.INTERVIEW
//                        && c.getInterviewDate() != null)
//                .collect(Collectors.toList());
//
//        return interviews.stream().map(c -> {
//            Map<String, Object> m = new java.util.LinkedHashMap<>();
//            m.put("id",            c.getId());
//            m.put("jobTitle",      c.getJobPosting() != null ? c.getJobPosting().getTitle() : "");
//            m.put("round",         "Round 1");
//            m.put("interviewType", "Technical Interview");
//            m.put("scheduledDate", c.getInterviewDate().toLocalDate());
//            m.put("scheduledTime", c.getInterviewDate().toLocalTime().toString());
//            return m;
//        }).collect(Collectors.toList());
//    }
//
//    // ── Helpers ───────────────────────────────────────────────────────────────
//    private String firstName(String fullName) {
//        if (fullName == null || fullName.isBlank()) return "";
//        String[] parts = fullName.trim().split("\\s+");
//        return parts[0];
//    }
//
//    private String lastName(String fullName) {
//        if (fullName == null || fullName.isBlank()) return "";
//        String[] parts = fullName.trim().split("\\s+");
//        return parts.length > 1 ? parts[parts.length - 1] : "";
//    }
//}


package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Candidate;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.CandidateRepository;
import com.revtalent.revtalent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateDashboardService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;

    // ── Profile ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return Map.of(
                "id",        user.getId(),
                "name",      user.getName() != null ? user.getName() : "",
                "email",     user.getEmail(),
                "username",  user.getUsername(),
                "role",      user.getRole().name(),
                "firstName", firstName(user.getName()),
                "lastName",  lastName(user.getName())
        );
    }

    // ── Applications — find all Candidate rows matching this user's email ─────
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getApplications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Candidate> candidates = candidateRepository.findByEmail(user.getEmail());

        return candidates.stream().map(c -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id",              c.getId());
            m.put("jobId",           c.getJobPosting() != null ? c.getJobPosting().getId() : null);
            m.put("jobTitle",        c.getJobPosting() != null ? c.getJobPosting().getTitle() : "");
            m.put("department",      c.getJobPosting() != null && c.getJobPosting().getDepartment() != null
                    ? c.getJobPosting().getDepartment().getName() : "");
            m.put("status",          c.getStatus().name());
            m.put("appliedDate",     c.getAppliedAt());
            m.put("phone",           c.getPhone());
            m.put("interviewDate",   c.getInterviewDate());
            m.put("interviewerName", c.getInterviewer() != null ? c.getInterviewer().getName() : null);
            return m;
        }).collect(Collectors.toList());
    }

    // ── Upcoming interviews — Candidate rows where status = INTERVIEW ─────────
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUpcomingInterviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Candidate> interviews = candidateRepository.findByEmail(user.getEmail())
                .stream()
                .filter(c -> c.getStatus() == Candidate.Status.INTERVIEW
                        && c.getInterviewDate() != null)
                .collect(Collectors.toList());

        return interviews.stream().map(c -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id",            c.getId());
            m.put("jobTitle",      c.getJobPosting() != null ? c.getJobPosting().getTitle() : "");
            m.put("department",    c.getJobPosting() != null && c.getJobPosting().getDepartment() != null
                    ? c.getJobPosting().getDepartment().getName() : "");
            m.put("round",         "Round 1");
            m.put("interviewType", "Technical Interview");
            m.put("interviewDate", c.getInterviewDate());
            m.put("scheduledDate", c.getInterviewDate().toLocalDate());
            m.put("scheduledTime", c.getInterviewDate().toLocalTime().toString());
            m.put("interviewerName", c.getInterviewer() != null ? c.getInterviewer().getName() : null);
            return m;
        }).collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private String lastName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
}