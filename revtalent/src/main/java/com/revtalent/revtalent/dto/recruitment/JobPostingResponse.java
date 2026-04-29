package com.revtalent.revtalent.dto.recruitment;

import com.revtalent.revtalent.model.JobPosting.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingResponse {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private Integer vacancies;
    private Status status;
    private String departmentName;
    private String createdByName;
    private LocalDate postedOn;
    private LocalDate closedOn;
}