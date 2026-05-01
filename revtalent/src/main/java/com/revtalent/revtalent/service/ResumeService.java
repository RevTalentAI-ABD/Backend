package com.revtalent.revtalent.service;

import com.revtalent.revtalent.model.Candidate;
import com.revtalent.revtalent.model.mongo.Resume;
import com.revtalent.revtalent.repository.CandidateRepository;
import com.revtalent.revtalent.repositorygit .ResumeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final CandidateRepository candidateRepository;

    public Resume uploadResumeFile(Long candidateId, MultipartFile file) {

        try {
            // 🔹 Fetch candidate
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));

            // 🔹 Upload directory
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // create folder if not exists
            }

            // 🔹 Save file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File savedFile = new File(uploadDir + fileName);
            file.transferTo(savedFile);

            // 🔹 Dummy parsed text (we'll upgrade later)
            String parsedText = ResumeParser.extractText(savedFile);

            // 🔹 Save in MongoDB
            Resume resume = Resume.builder()
                    .candidateId(candidateId)
                    .fileUrl(savedFile.getAbsolutePath())
                    .parsedText(parsedText)
                    .build();

            Resume savedResume = resumeRepository.save(resume);

            // 🔹 Update MySQL candidate
            candidate.setResumeMongoId(savedResume.getId());
            candidateRepository.save(candidate);

            return savedResume;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}