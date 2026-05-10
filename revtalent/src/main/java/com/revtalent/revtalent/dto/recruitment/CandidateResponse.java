package com.revtalent.revtalent.dto.recruitment;

import com.revtalent.revtalent.model.Candidate.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String githubUrl;
    private Status status;
    private Long jobId;
    private String jobTitle;
    private String departmentName;   // ← added
    private String resumeMongoId;
    private LocalDateTime interviewDate;
    private LocalDate offerDate;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private Long   interviewerId;
    private String interviewerName;
}