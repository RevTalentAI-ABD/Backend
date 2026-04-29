package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.JobPosting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    // ✅ EntityGraph to avoid circular reference
    @EntityGraph(attributePaths = {
            "department",
            "createdBy",
            "createdBy.user"
    })
    List<JobPosting> findAll();
}