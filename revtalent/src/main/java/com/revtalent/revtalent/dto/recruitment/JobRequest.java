package com.revtalent.revtalent.dto.recruitment;

import lombok.Data;

@Data
public class JobRequest {
    private String title;
    private String description;
    private String requirements;
    private Integer vacancies;
    private String status;
}