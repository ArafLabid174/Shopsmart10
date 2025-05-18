package com.shopsmart.shopsmart_server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsmart.shopsmart_server.dto.AddToCartRequest;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.CartItem;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.CartRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public CartService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public ApiResponse<CartItem> addToCart(String userId, AddToCartRequest request) {
        // Validate request
        if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
            return ApiResponse.error("Product ID is required");
        }
        
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            return ApiResponse.error("Product name is required");
        }
        
        if (request.getProductLink() == null || request.getProductLink().trim().isEmpty()) {
            return ApiResponse.error("Product link is required");
        }
        
        // Check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        // Create and save cart item
        CartItem cartItem = new CartItem(
            userId,
            request.getProductId(),
            request.getProductName(),
            request.getProductDescription(),
            request.getProductBrand(),
            request.getProductPrice(),
            request.getProductLink(),
            request.getImageLink()
        );
        
        CartItem savedCartItem = cartRepository.save(cartItem);
        
        return ApiResponse.success("Item added to cart successfully", savedCartItem);
    }
    
    public ApiResponse<List<CartItem>> getCartByUserId(String userId) {
        // Check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        
        return ApiResponse.success("Cart items retrieved successfully", cartItems);
    }
    
    @Transactional
    public ApiResponse<Void> removeFromCart(String userId, String productId) {
        // Check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        cartRepository.deleteByUserIdAndProductId(userId, productId);
        
        return ApiResponse.success("Item removed from cart successfully", null);
    }
}