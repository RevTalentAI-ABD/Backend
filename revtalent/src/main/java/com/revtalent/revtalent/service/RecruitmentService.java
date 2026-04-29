package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.recruitment.JobPostingResponse;
import com.revtalent.revtalent.dto.recruitment.JobRequest;
import com.revtalent.revtalent.model.JobPosting;
import com.revtalent.revtalent.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final JobPostingRepository repo;

    // ✅ Mapper
    private JobPostingResponse mapToResponse(JobPosting j) {
        JobPostingResponse res = new JobPostingResponse();
        res.setId(j.getId());
        res.setTitle(j.getTitle());
        res.setDescription(j.getDescription());
        res.setRequirements(j.getRequirements());
        res.setVacancies(j.getVacancies());
        res.setStatus(j.getStatus());
        res.setPostedOn(j.getPostedOn());
        res.setClosedOn(j.getClosedOn());

        // ✅ Null checks for optional relations
        if (j.getDepartment() != null) {
            res.setDepartmentName(j.getDepartment().getName());
        }
        if (j.getCreatedBy() != null && j.getCreatedBy().getUser() != null) {
            res.setCreatedByName(j.getCreatedBy().getUser().getName());
        }

        return res;
    }

    // ✅ Get All Jobs
    public List<JobPostingResponse> getAllJobs() {
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Create Job
    public JobPostingResponse createJob(JobRequest req) {
        JobPosting j = new JobPosting();
        j.setTitle(req.getTitle());
        j.setDescription(req.getDescription());
        j.setRequirements(req.getRequirements());
        j.setVacancies(req.getVacancies());
        j.setStatus(JobPosting.Status.valueOf(req.getStatus()));
        return mapToResponse(repo.save(j));
    }
}