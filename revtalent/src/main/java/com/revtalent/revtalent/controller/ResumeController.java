package com.revtalent.revtalent.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import com.revtalent.revtalent.model.mongo.Resume;
import com.revtalent.revtalent.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ ADD THIS

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(
            value = "/{candidateId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("permitAll()")
    public Resume upload(
            @PathVariable Long candidateId,

            @RequestPart("file")   // 🔥 CHANGE THIS (important)
            @Schema(type = "string", format = "binary")
            MultipartFile file
    ) {
        return resumeService.uploadResumeFile(candidateId, file);
    }
}