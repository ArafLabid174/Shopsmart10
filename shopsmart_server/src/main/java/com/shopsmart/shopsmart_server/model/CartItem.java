package com.shopsmart.shopsmart_server.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cart_items")
public class CartItem {
    
    @Id
    private String id;
    
    private String userId;
    private Long productId;
    private String productName;
    private String productLink;
    private LocalDateTime createdAt;
    
    // Default constructor
    public CartItem() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with fields
    public CartItem(String userId, Long productId, String productName, String productLink) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productLink = productLink;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductLink() {
        return productLink;
    }
    
    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}