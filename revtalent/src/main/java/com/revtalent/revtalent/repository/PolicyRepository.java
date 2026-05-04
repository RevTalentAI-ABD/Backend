package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.mongo.Policy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PolicyRepository extends MongoRepository<Policy, String> {
}