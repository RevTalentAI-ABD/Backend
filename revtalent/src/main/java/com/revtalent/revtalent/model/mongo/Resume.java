package com.revtalent.revtalent.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    private String id;

    private Long candidateId;   // link with MySQL Candidate

    private String fileUrl;

    private String parsedText;

    private double[] embeddings; // for AI later (optional now)
}