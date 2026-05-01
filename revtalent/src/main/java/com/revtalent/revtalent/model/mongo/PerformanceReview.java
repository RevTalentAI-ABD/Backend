package com.revtalent.revtalent.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "performance_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReview {

    @Id
    private String id;

    private Long employeeId;     // MySQL Employee
    private Long reviewerId;     // Manager

    private String cycle;        // e.g. "2025-Q1"

    private String ratings;      // JSON string or simple text

    private String feedback;
}