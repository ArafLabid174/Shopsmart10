package com.shopsmart.shopsmart_server.dto;

public class AddToCartRequest {
    private String productId;  // UPC code
    private String productName;
    private String productDescription;
    private String productBrand;
    private Double productPrice;
    private String productLink;
    private String imageLink;
    
    // Default constructor
    public AddToCartRequest() {
    }
    
    // Constructor with fields
    public AddToCartRequest(String productId, String productName, String productDescription,
                          String productBrand, Double productPrice, String productLink, String imageLink) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productBrand = productBrand;
        this.productPrice = productPrice;
        this.productLink = productLink;
        this.imageLink = imageLink;
    }
    
    // Getters and Setters
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
}