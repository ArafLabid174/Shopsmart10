package com.shopsmart.shopsmart_server.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cart_items")
public class CartItem {
    
    @Id
    private String id;
    
    private String userId;
    private String productId;  // UPC code
    private String productName;
    private String productDescription;
    private String productBrand;
    private Double productPrice;
    private String productLink;
    private String imageLink;
    private LocalDateTime createdAt;
    
    // Default constructor
    public CartItem() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with fields
    public CartItem(String userId, String productId, String productName, String productDescription,
                   String productBrand, Double productPrice, String productLink, String imageLink) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productBrand = productBrand;
        this.productPrice = productPrice;
        this.productLink = productLink;
        this.imageLink = imageLink;
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
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public String getProductBrand() {
        return productBrand;
    }
    
    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }
    
    public Double getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }
    
    public String getProductLink() {
        return productLink;
    }
    
    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }
    
    public String getImageLink() {
        return imageLink;
    }
    
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}