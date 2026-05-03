package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.mongo.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {

    List<ChatHistory> findByUserId(Long userId);
}