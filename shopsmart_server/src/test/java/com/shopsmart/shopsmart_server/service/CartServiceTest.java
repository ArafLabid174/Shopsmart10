package com.shopsmart.shopsmart_server.service;

import com.shopsmart.shopsmart_server.dto.AddToCartRequest;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.CartItem;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.CartRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addToCart_shouldReturnError_whenProductIdIsNull() {
        // Arrange
        String userId = "testUser";
        AddToCartRequest request = new AddToCartRequest(
                null, // productId
                "Test Product",
                "Description",
                "Brand",
                19.99,
                "http://example.com",
                "http://example.com/img.jpg"
        );

        // Act
        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        // Assert
        assertEquals("Product ID is required", response.getMessage());
    }

    @Test
    void addToCart_shouldReturnError_whenProductNameIsNullOrEmpty() {
        // Arrange
        String userId = "testUser";
        AddToCartRequest request = new AddToCartRequest(
                "1", // productId as String
                "", // Empty productName
                "Description",
                "Brand",
                19.99,
                "http://example.com",
                "http://example.com/img.jpg"
        );

        // Act
        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        // Assert
        assertEquals("Product name is required", response.getMessage());
    }

    @Test
    void addToCart_shouldReturnError_whenProductLinkIsNullOrEmpty() {
        // Arrange
        String userId = "testUser";
        AddToCartRequest request = new AddToCartRequest(
                "1", // productId
                "Test Product",
                "Description",
                "Brand",
                19.99,
                "", // Empty productLink
                "http://example.com/img.jpg"
        );

        // Act
        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        // Assert
        assertEquals("Product link is required", response.getMessage());
    }

    @Test
    void addToCart_shouldReturnError_whenUserNotFound() {
        // Arrange
        String userId = "testUser";
        AddToCartRequest request = new AddToCartRequest(
                "1",
                "Test Product",
                "Description",
                "Brand",
                19.99,
                "http://example.com",
                "http://example.com/img.jpg"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        // Assert
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void addToCart_shouldReturnSuccess_whenItemIsAdded() {
        // Arrange
        String userId = "testUser";
        AddToCartRequest request = new AddToCartRequest(
                "1",
                "Test Product",
                "Description",
                "Brand",
                19.99,
                "http://example.com",
                "http://example.com/img.jpg"
        );
        CartItem cartItem = new CartItem(
                userId,
                "1",
                "Test Product",
                "Description",
                "Brand",
                19.99,
                "http://example.com",
                "http://example.com/img.jpg"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // Act
        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        // Assert
        assertEquals("Item added to cart successfully", response.getMessage());
        assertEquals(cartItem, response.getData());
    }

    @Test
    void getCartByUserId_shouldReturnError_whenUserNotFound() {
        // Arrange
        String userId = "testUser";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse<List<CartItem>> response = cartService.getCartByUserId(userId);

        // Assert
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void getCartByUserId_shouldReturnCartItems_whenUserExists() {
        // Arrange
        String userId = "testUser";
        List<CartItem> cartItems = Collections.singletonList(new CartItem(
                userId,
                "1",
                "Test Product",
                "Description",
                "Brand",
                19.99,
                "http://example.com",
                "http://example.com/img.jpg"
        ));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(cartRepository.findByUserId(userId)).thenReturn(cartItems);

        // Act
        ApiResponse<List<CartItem>> response = cartService.getCartByUserId(userId);

        // Assert
        assertEquals("Cart items retrieved successfully", response.getMessage());
        assertEquals(cartItems, response.getData());
    }

    @Test
    void removeFromCart_shouldReturnError_whenUserNotFound() {
        // Arrange
        String userId = "testUser";
        String productId = "1";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Void> response = cartService.removeFromCart(userId, productId);

        // Assert
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void removeFromCart_shouldReturnSuccess_whenItemRemovedSuccessfully() {
        // Arrange
        String userId = "testUser";
        String productId = "1";

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        // Act
        ApiResponse<Void> response = cartService.removeFromCart(userId, productId);

        // Assert
        assertEquals("Item removed from cart successfully", response.getMessage());
        verify(cartRepository, times(1)).deleteByUserIdAndProductId(userId, productId);
    }
}