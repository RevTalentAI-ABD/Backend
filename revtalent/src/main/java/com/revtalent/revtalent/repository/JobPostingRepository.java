package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
}