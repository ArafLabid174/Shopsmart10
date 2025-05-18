package com.shopsmart.shopsmart_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopsmart.shopsmart_server.model.LookupHistory;

@Repository
public interface LookupHistoryRepository extends MongoRepository<LookupHistory, String> {
    List<LookupHistory> findByUserId(String userId);
    void deleteByUserIdAndUpcCode(String userId, String upcCode);
}