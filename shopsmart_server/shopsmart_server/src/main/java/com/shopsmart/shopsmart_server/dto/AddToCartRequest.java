package com.shopsmart.shopsmart_server.dto;

public class AddToCartRequest {
    private Long productId;
    private String productName;
    private String productLink;
    
    // Default constructor
    public AddToCartRequest() {
    }
    
    // Constructor with fields
    public AddToCartRequest(Long productId, String productName, String productLink) {
        this.productId = productId;
        this.productName = productName;
        this.productLink = productLink;
    }
    
    // Getters and Setters
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
}