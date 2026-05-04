//package com.revtalent.revtalent.controller;
//
//import com.revtalent.revtalent.service.ResumeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/resume")
//@RequiredArgsConstructor
//@CrossOrigin("*")
//public class ResumeController {
//
//    private final ResumeService resumeService;
//
//    /**
//     * Called by ApplyForm (public) — analyzes resume AND links it to a candidate.
//     * POST /api/resume/upload
//     * Form params: file (MultipartFile), candidateId (Long)
//     */
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, Object>> uploadAndLink(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("candidateId") Long candidateId) {
//
//        Map<String, Object> result = resumeService.uploadAndLink(file, candidateId);
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * Called by HR dashboard — analyze only, no candidate link.
//     * POST /api/resume/analyze
//     */
//    @PostMapping("/analyze")
//    public ResponseEntity<Map<String, Object>> analyzeResume(
//            @RequestParam("file") MultipartFile file) {
//
//        return ResponseEntity.ok(resumeService.analyzeResume(file));
//    }
//
//    /**
//     * Get resume metadata for a candidate.
//     * GET /api/resume/candidate/{candidateId}
//     */
//    @GetMapping("/candidate/{candidateId}")
//    public ResponseEntity<Map<String, Object>> getResumeByCandidate(
//            @PathVariable Long candidateId) {
//
//        return ResponseEntity.ok(resumeService.getResumeByCandidate(candidateId));
//    }
//
//    /**
//     * Download/view the actual resume file.
//     * GET /api/resume/candidate/{candidateId}/download
//     */
//    @GetMapping("/candidate/{candidateId}/download")
//    public ResponseEntity<byte[]> downloadResume(
//            @PathVariable Long candidateId) {
//
//        ResumeService.ResumeFile resumeFile = resumeService.downloadResume(candidateId);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_TYPE, resumeFile.contentType())
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "inline; filename=\"" + resumeFile.filename() + "\"")
//                .body(resumeFile.data());
//    }
//}

package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ResumeController {

    private final ResumeService resumeService;

    /**
     * Called by ApplyForm (public) — analyzes resume AND links it to a candidate.
     * POST /api/resume/upload
     * Form params: file (MultipartFile), candidateId (Long)
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAndLink(
            @RequestParam("file") MultipartFile file,
            @RequestParam("candidateId") Long candidateId) {

        Map<String, Object> result = resumeService.uploadAndLink(file, candidateId);
        return ResponseEntity.ok(result);
    }

    /**
     * Called by HR dashboard — analyze only, no candidate link.
     * POST /api/resume/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeResume(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(resumeService.analyzeResume(file));
    }

    /**
     * Get resume metadata for a candidate.
     * GET /api/resume/candidate/{candidateId}
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<Map<String, Object>> getResumeByCandidate(
            @PathVariable Long candidateId) {

        return ResponseEntity.ok(resumeService.getResumeByCandidate(candidateId));
    }

    /**
     * Download/view the actual resume file.
     * GET /api/resume/candidate/{candidateId}/download
     */
    @GetMapping("/candidate/{candidateId}/download")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long candidateId) {

        ResumeService.ResumeFile resumeFile = resumeService.downloadResume(candidateId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, resumeFile.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resumeFile.filename() + "\"")
                .body(resumeFile.data());
    }
}