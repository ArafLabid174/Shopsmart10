package com.shopsmart.shopsmart_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopsmart.shopsmart_server.model.DeepSearchResult;

@Repository
public interface DeepSearchRepository extends MongoRepository<DeepSearchResult, String> {
    List<DeepSearchResult> findByUserId(String userId);
    List<DeepSearchResult> findByUserIdAndSearchTerm(String userId, String searchTerm);
}