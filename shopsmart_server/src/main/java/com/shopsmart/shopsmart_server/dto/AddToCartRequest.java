<<<<<<< HEAD
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
=======
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
>>>>>>> f05afc47162a15a3034e3b01c500459bdef193e5
}