<<<<<<< HEAD
package com.shopsmart.shopsmart_server.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lookup_history")
public class LookupHistory {
    
    @Id
    private String id;
    
    private String userId;
    private String upcCode;
    private String lookupUrl;
    private String productTitle;
    private String productDescription;
    private String productBrand;
    private Double productPrice;
    private String productLink;
    private String imageLink;
    private LocalDateTime createdAt;
    
    // Default constructor
    public LookupHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with fields
    public LookupHistory(String userId, String upcCode, String lookupUrl, String productTitle, 
                        String productDescription, String productBrand, Double productPrice, 
                        String productLink, String imageLink) {
        this.userId = userId;
        this.upcCode = upcCode;
        this.lookupUrl = lookupUrl;
        this.productTitle = productTitle;
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
    
    public String getUpcCode() {
        return upcCode;
    }
    
    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }
    
    public String getLookupUrl() {
        return lookupUrl;
    }
    
    public void setLookupUrl(String lookupUrl) {
        this.lookupUrl = lookupUrl;
    }
    
    public String getProductTitle() {
        return productTitle;
    }
    
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
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
=======
package com.shopsmart.shopsmart_server.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lookup_history")
public class LookupHistory {
    
    @Id
    private String id;
    
    private String userId;
    private String upcCode;
    private String lookupUrl;
    private String productTitle;
    private String productBrand;
    private String productImage;
    private Double productPrice;
    private LocalDateTime createdAt;
    
    // Default constructor
    public LookupHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with fields
    public LookupHistory(String userId, String upcCode, String lookupUrl, String productTitle, 
                         String productBrand, String productImage, Double productPrice) {
        this.userId = userId;
        this.upcCode = upcCode;
        this.lookupUrl = lookupUrl;
        this.productTitle = productTitle;
        this.productBrand = productBrand;
        this.productImage = productImage;
        this.productPrice = productPrice;
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
    
    public String getUpcCode() {
        return upcCode;
    }
    
    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }
    
    public String getLookupUrl() {
        return lookupUrl;
    }
    
    public void setLookupUrl(String lookupUrl) {
        this.lookupUrl = lookupUrl;
    }
    
    public String getProductTitle() {
        return productTitle;
    }
    
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
    
    public String getProductBrand() {
        return productBrand;
    }
    
    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }
    
    public String getProductImage() {
        return productImage;
    }
    
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    
    public Double getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
>>>>>>> f05afc47162a15a3034e3b01c500459bdef193e5
}