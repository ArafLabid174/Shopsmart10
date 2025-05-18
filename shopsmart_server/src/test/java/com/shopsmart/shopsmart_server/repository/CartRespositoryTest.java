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
        CartItem item1 = new CartItem(
                "userId1",
                "101", // productId as String
                "Test Product 1",
                "Description 1",
                "Brand 1",
                5.99,
                "http://example.com/1",
                "http://example.com/img1.jpg"
        );
        CartItem item2 = new CartItem(
                "userId2",
                "102",
                "Test Product 2",
                "Description 2",
                "Brand 2",
                7.49,
                "http://example.com/2",
                "http://example.com/img2.jpg"
        );
        CartItem item3 = new CartItem(
                "userId1",
                "103",
                "Test Product 3",
                "Description 3",
                "Brand 3",
                10.25,
                "http://example.com/3",
                "http://example.com/img3.jpg"
        );

        cartRepository.save(item1);
        cartRepository.save(item2);
        cartRepository.save(item3);

        // Act: Fetch CartItems by userId
        List<CartItem> items = cartRepository.findByUserId("userId1");

        // Assert: Verify the results
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getProductId().equals("101")));
        assertTrue(items.stream().anyMatch(item -> item.getProductId().equals("103")));
    }

    @Test
    void shouldDeleteCartItemByUserIdAndProductId() {
        // Arrange: Create and save a CartItem
        CartItem item = new CartItem(
                "userId1",
                "201",
                "Test Product",
                "Description",
                "Brand",
                14.99,
                "http://example.com",
                "http://example.com/img201.jpg"
        );
        cartRepository.save(item);

        // Act: Delete the CartItem
        cartRepository.deleteByUserIdAndProductId("userId1", "201");

        // Assert: Verify the item was deleted
        List<CartItem> items = cartRepository.findByUserId("userId1");
        assertTrue(items.isEmpty());
    }
}