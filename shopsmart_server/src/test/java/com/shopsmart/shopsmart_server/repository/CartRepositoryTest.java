package com.shopsmart.shopsmart_server.repository;

import com.shopsmart.shopsmart_server.model.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ActiveProfiles("test")  // Assuming you have a test profile that configures MongoDB connections
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    void shouldFindCartItemsByUserId() {
        // Arrange: Create and save some CartItems
        CartItem item1 = new CartItem("userId1", 101L, "Test Product 1", "http://example.com/1");
        CartItem item2 = new CartItem("userId2", 102L, "Test Product 2", "http://example.com/2");
        CartItem item3 = new CartItem("userId1", 103L, "Test Product 3", "http://example.com/3");

        cartRepository.save(item1);
        cartRepository.save(item2);
        cartRepository.save(item3);

        // Act: Fetch CartItems by userId
        List<CartItem> items = cartRepository.findByUserId("userId1");

        // Assert: Verify the results
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getProductId().equals(101L)));
        assertTrue(items.stream().anyMatch(item -> item.getProductId().equals(103L)));
    }

    @Test
    void shouldDeleteCartItemByUserIdAndProductId() {
        // Arrange: Create and save a CartItem
        CartItem item = new CartItem("userId1", 201L, "Test Product", "http://example.com");
        cartRepository.save(item);

        // Act: Delete the CartItem
        cartRepository.deleteByUserIdAndProductId("userId1", 201L);

        // Assert: Verify the item was deleted
        List<CartItem> items = cartRepository.findByUserId("userId1");
        assertTrue(items.isEmpty());
    }
}