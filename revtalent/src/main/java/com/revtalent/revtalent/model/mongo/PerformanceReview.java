package com.revtalent.revtalent.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "performance_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReview {

    @Id
    private String id;

    private Long employeeId;       // MySQL Employee ID
    private String employeeName;   // Denormalised for quick display
    private Long reviewerId;       // Manager's employee ID
    private String reviewerName;   // Manager name

    private String cycle;          // e.g. "2025-Q2"
    private String reviewPeriod;   // e.g. "April 2025 - June 2025"

    // Ratings (1-5)
    private Integer technicalSkills;
    private Integer communication;
    private Integer teamwork;
    private Integer problemSolving;
    private Integer leadership;
    private Integer punctuality;
    private Double  overallRating;  // computed average

    // Text feedback
    private String strengths;
    private String improvements;
    private String goals;
    private String managerComments;

    // Status: DRAFT | SUBMITTED | ACKNOWLEDGED
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}