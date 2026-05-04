package com.revtalent.revtalent.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScreeningResultDTO {

    private Long candidateId;
    private String name;
    private double score;
}