package com.shopsmart.shopsmart_server.repository;

import com.shopsmart.shopsmart_server.model.LookupHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ActiveProfiles("test")  // Assuming you have a test profile that configures MongoDB connections
class LookupHistoryRepositoryTest {

    @Autowired
    private LookupHistoryRepository lookupHistoryRepository;

    @BeforeEach
    void clearDatabaseBefore() {
        lookupHistoryRepository.deleteAll();
    }
    @Test
    void shouldFindLookupHistoryByUserId() {
        // Arrange: Create and save some LookupHistory entries
        LookupHistory history1 = new LookupHistory(
                "userId1",
                "1234567890",
                "http://example.com/lookup1",
                "Product1",
                "Description1",
                "Brand1",
                10.0,
                "http://example.com/product1",
                "ImageURL1"
        );
        LookupHistory history2 = new LookupHistory(
                "userId2",
                "0987654321",
                "http://example.com/lookup2",
                "Product2",
                "Description2",
                "Brand2",
                20.0,
                "http://example.com/product2",
                "ImageURL2"
        );
        LookupHistory history3 = new LookupHistory(
                "userId1",
                "1122334455",
                "http://example.com/lookup3",
                "Product3",
                "Description3",
                "Brand3",
                30.0,
                "http://example.com/product3",
                "ImageURL3"
        );

        lookupHistoryRepository.save(history1);
        lookupHistoryRepository.save(history2);
        lookupHistoryRepository.save(history3);

        // Act: Fetch LookupHistory by userId
        List<LookupHistory> histories = lookupHistoryRepository.findByUserId("userId1");

        // Assert: Verify the results
        assertEquals(2, histories.size());
        assertTrue(histories.stream().anyMatch(h -> h.getUpcCode().equals("1234567890")));
        assertTrue(histories.stream().anyMatch(h -> h.getUpcCode().equals("1122334455")));
    }

    @Test
    void shouldDeleteLookupHistoryByUserIdAndUpcCode() {
        // Arrange: Create and save a LookupHistory entry
        LookupHistory history = new LookupHistory(
                "userId10",
                "9876543211",
                "http://example.com/lookup",
                "Product",
                "Description",
                "Brand",
                15.0,
                "http://example.com/product",
                "ImageURL"
        );
        lookupHistoryRepository.save(history);

        // Act: Delete the LookupHistory entry
        lookupHistoryRepository.deleteByUserIdAndUpcCode("userId10", "9876543211");

        // Assert: Verify the entry is deleted
        List<LookupHistory> histories = lookupHistoryRepository.findByUserId("userId10");
//        System.out.println("There is a user " + histories);
        assertTrue(histories.isEmpty());
    }
}