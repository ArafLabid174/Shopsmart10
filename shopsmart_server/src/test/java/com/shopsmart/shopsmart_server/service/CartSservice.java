package com.shopsmart.shopsmart_server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.shopsmart.shopsmart_server.dto.AddToCartRequest;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.CartItem;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.CartRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @InjectMocks
    private CartService cartService;

    private final String userId = "testUserId";

    @BeforeEach
    void setup() {
        // no-op; field injection handled by @InjectMocks
    }

    private AddToCartRequest buildValidRequest() {
        AddToCartRequest req = new AddToCartRequest();
        req.setProductId("761531127634");
        req.setProductName("Used Apple MacBook 12 Laptop with Retina Display");
        req.setProductDescription("description");
        req.setProductBrand("Apple");
        req.setProductPrice(269.97);
        req.setProductLink("https://link");
        req.setImageLink("https://img");
        return req;
    }

    @Test
    void testAddToCart_UserNotFound() {
        AddToCartRequest request = buildValidRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        assertFalse(response.isSuccess());
        assertEquals("User not found", response.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(cartRepository);
    }

    @Test
    void testAddToCart_MissingProductId() {
        AddToCartRequest request = buildValidRequest();
        request.setProductId(null);

        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        assertFalse(response.isSuccess());
        assertEquals("Product ID is required", response.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(cartRepository);
    }

    @Test
    void testAddToCart_MissingProductName() {
        AddToCartRequest request = buildValidRequest();
        request.setProductName("");

        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        assertFalse(response.isSuccess());
        assertEquals("Product name is required", response.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(cartRepository);
    }

    @Test
    void testAddToCart_MissingProductLink() {
        AddToCartRequest request = buildValidRequest();
        request.setProductLink("    ");

        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        assertFalse(response.isSuccess());
        assertEquals("Product link is required", response.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(cartRepository);
    }

    @Test
    void testAddToCart_Success() {
        AddToCartRequest request = buildValidRequest();
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        CartItem savedCartItem = new CartItem(
                userId, request.getProductId(), request.getProductName(),
                request.getProductDescription(), request.getProductBrand(),
                request.getProductPrice(), request.getProductLink(), request.getImageLink());
        savedCartItem.setId("testCartItemId");

        when(cartRepository.save(any(CartItem.class))).thenReturn(savedCartItem);

        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        assertTrue(response.isSuccess());
        assertEquals("Item added to cart successfully", response.getMessage());
        assertEquals(savedCartItem, response.getData());

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(captor.capture());
        CartItem captured = captor.getValue();
        assertEquals(userId, captured.getUserId());
        assertEquals(request.getProductId(), captured.getProductId());
    }


    @Test
    void testAddToCart_Failure() {
        AddToCartRequest request = buildValidRequest();
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(cartRepository.save(any(CartItem.class))).thenThrow(new RuntimeException("Database error"));

        ApiResponse<CartItem> response = cartService.addToCart(userId, request);

        assertFalse(response.isSuccess());
        assertEquals("Failed to add item to cart", response.getMessage());
    }
}