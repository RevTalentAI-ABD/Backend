package com.revtalent.revtalent.dto.recruitment;

import lombok.Data;

@Data
public class CandidateRequest {
    private Long jobId;      // which job they're applying for
    private String name;
    private String email;
    private String phone;
}