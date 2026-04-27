package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}