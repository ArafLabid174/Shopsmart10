package com.shopsmart.shopsmart_server.controller;

import com.shopsmart.shopsmart_server.dto.AddToCartRequest;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.CartItem;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.CartRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // Set up test data; for example, add a test user
        User user = new User();
        user.setId("user123");
        user.setEmail("testuser@example.com");
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        cartRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenValidAddToCartRequest_thenItemShouldBeAdded() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId("1"); // as String
        request.setProductName("Test Product");
        request.setProductLink("http://example.com/test-product");

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/cart/user123/add", new HttpEntity<>(request), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List<CartItem> cartItems = cartRepository.findByUserId("user123");
        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.get(0).getProductId()).isEqualTo("1"); // as String
        assertThat(cartItems.get(0).getProductName()).isEqualTo("Test Product");
    }


    @Test
    void whenValidUserId_thenShouldReturnCartItems() {
        // Arrange
        CartItem cartItem = new CartItem(
                "user123", "1", "Test Product", "Description", "Brand",
                19.99, "http://example.com/test-product", "http://example.com/img.jpg"
        );
        cartRepository.save(cartItem);

        // Act
        ResponseEntity<ApiResponse<List<CartItem>>> response =
                restTemplate.exchange(
                        "/api/cart/user123",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ApiResponse<List<CartItem>>>() {}
                );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getProductId()).isEqualTo("1");
    }

    @Test
    void whenValidRemoveFromCartRequest_thenItemShouldBeRemoved() {
        // Arrange
        CartItem cartItem = new CartItem("user123", "1", "Test Product", "Description", "Brand", 19.99, "http://example.com/test-product", "http://example.com/img.jpg");
        cartRepository.save(cartItem);

        // Act
        ResponseEntity<String> response = restTemplate.exchange("/api/cart/user123/remove/1", HttpMethod.DELETE, null, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cartRepository.findByUserId("user123")).isEmpty();
    }
}