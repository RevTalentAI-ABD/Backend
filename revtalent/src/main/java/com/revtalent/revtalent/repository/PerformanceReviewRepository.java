package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.mongo.PerformanceReview;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PerformanceReviewRepository extends MongoRepository<PerformanceReview, String> {

    List<PerformanceReview> findByEmployeeId(Long employeeId);
}