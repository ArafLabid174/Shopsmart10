package com.shopsmart.shopsmart_server.controller;

import com.shopsmart.shopsmart_server.dto.AddToCartRequest;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.CartItem;
import com.shopsmart.shopsmart_server.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addToCart_shouldReturnCreatedStatus_whenAddSucceeds() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId("761531127634");
        request.setProductName("Used Apple MacBook 12 Laptop with Retina Display");
        request.setProductDescription("The MacBook features a 12 Retina display...");
        request.setProductBrand("Apple");
        request.setProductPrice(269.97);
        request.setProductLink("https://www.upcitemdb.com/norob/alink/?id=z2u2y2z2y2x27464v2&tid=1&seq=1747308873&plt=2ad25dcc627502631af43bb20b7c8cd6");
        request.setImageLink("https://i5.walmartimages.com/asr/65d15e64-59a3-41ae-91ce-1f573b240c5a.2c69475aa86e45f69584c2216b659862.jpeg");

        CartItem mockItem = new CartItem();
        mockItem.setId("6825de024c4d660198dc13e9");
        mockItem.setUserId("68210dfeb7f39d47f4437f77");
        mockItem.setProductId("761531127634");
        mockItem.setProductName("Used Apple MacBook 12 Laptop with Retina Display");
        mockItem.setProductDescription("The MacBook features a 12 Retina display...");
        mockItem.setProductBrand("Apple");
        mockItem.setProductPrice(269.97);
        mockItem.setProductLink("https://www.upcitemdb.com/norob/alink/?id=z2u2y2z2y2x27464v2&tid=1&seq=1747308873&plt=2ad25dcc627502631af43bb20b7c8cd6");
        mockItem.setImageLink("https://i5.walmartimages.com/asr/65d15e64-59a3-41ae-91ce-1f573b240c5a.2c69475aa86e45f69584c2216b659862.jpeg");
        mockItem.setCreatedAt(LocalDateTime.of(2025, 5, 15, 18, 28, 50, 437_435_400));

        ApiResponse<CartItem> apiResponse = new ApiResponse<>(true, "Item added to cart successfully", mockItem);

        when(cartService.addToCart(any(String.class), any(AddToCartRequest.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<CartItem>> responseEntity = cartController.addToCart("68210dfeb7f39d47f4437f77", request);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void addToCart_shouldReturnBadRequestStatus_whenAddFails() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        ApiResponse<CartItem> apiResponse = new ApiResponse<>(false, "Failed to add item to cart", null);

        when(cartService.addToCart(any(String.class), any(AddToCartRequest.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<CartItem>> responseEntity = cartController.addToCart("68210dfeb7f39d47f4437f77", request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void getCartByUserId_shouldReturnOkStatus_whenCartRetrievedSuccessfully() {
        // Arrange
        CartItem mockItem = new CartItem();
        mockItem.setId("6825de024c4d660198dc13e9");
        mockItem.setUserId("68210dfeb7f39d47f4437f77");
        mockItem.setProductId("761531127634");
        mockItem.setProductName("Used Apple MacBook 12 Laptop with Retina Display");
        mockItem.setProductDescription("The MacBook features a 12 Retina display...");
        mockItem.setProductBrand("Apple");
        mockItem.setProductPrice(269.97);
        mockItem.setProductLink("https://www.upcitemdb.com/norob/alink/?id=z2u2y2z2y2x27464v2&tid=1&seq=1747308873&plt=2ad25dcc627502631af43bb20b7c8cd6");
        mockItem.setImageLink("https://i5.walmartimages.com/asr/65d15e64-59a3-41ae-91ce-1f573b240c5a.2c69475aa86e45f69584c2216b659862.jpeg");
        mockItem.setCreatedAt(java.time.LocalDateTime.of(2025, 5, 15, 18, 28, 50, 437_000_000));

        List<CartItem> mockItems = Collections.singletonList(mockItem);
        ApiResponse<List<CartItem>> apiResponse = new ApiResponse<>(true, "Cart items retrieved successfully", mockItems);

        when(cartService.getCartByUserId(any(String.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<List<CartItem>>> responseEntity = cartController.getCartByUserId("68210dfeb7f39d47f4437f77");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void getCartByUserId_shouldReturnBadRequestStatus_whenRetrievalFails() {
        // Arrange
        ApiResponse<List<CartItem>> apiResponse = new ApiResponse<>(false, "Failed to retrieve cart items", null);

        when(cartService.getCartByUserId(any(String.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<List<CartItem>>> responseEntity = cartController.getCartByUserId("68210dfeb7f39d47f4437f77");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void removeFromCart_shouldReturnOkStatus_whenRemoveSucceeds() {
        // Arrange
        ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Item removed from cart successfully", null);

        when(cartService.removeFromCart(any(String.class), any(String.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> responseEntity = cartController.removeFromCart("68210dfeb7f39d47f4437f77", "761531127634");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void removeFromCart_shouldReturnBadRequestStatus_whenRemoveFails() {
        // Arrange
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Failed to remove item from cart", null);

        when(cartService.removeFromCart(any(String.class), any(String.class))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> responseEntity = cartController.removeFromCart("68210dfeb7f39d47f4437f77", "761531127634");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }
}