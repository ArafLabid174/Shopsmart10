package com.shopsmart.shopsmart_server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsmart.shopsmart_server.dto.AddToCartRequest;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.CartItem;
import com.shopsmart.shopsmart_server.service.CartService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    @PostMapping("/{userId}/add")
    public ResponseEntity<ApiResponse<CartItem>> addToCart(
            @PathVariable String userId,
            @RequestBody AddToCartRequest request) {
        
        ApiResponse<CartItem> response = cartService.addToCart(userId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCartByUserId(@PathVariable String userId) {
        ApiResponse<List<CartItem>> response = cartService.getCartByUserId(userId);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable String userId,
            @PathVariable Long productId) {
        
        ApiResponse<Void> response = cartService.removeFromCart(userId, productId);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}