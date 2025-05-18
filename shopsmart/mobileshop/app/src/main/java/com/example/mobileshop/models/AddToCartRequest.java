package com.example.mobileshop.models;

import com.google.gson.annotations.SerializedName;

public class AddToCartRequest {
    @SerializedName("productId")
    private String productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("productDescription")
    private String productDescription;

    @SerializedName("productBrand")
    private String productBrand;    @SerializedName("productPrice")
    private Double productPrice;

    @SerializedName("productLink")
    private String productLink;

    @SerializedName("imageLink")
    private String imageLink;    public AddToCartRequest(String productId, String productName, String productDescription,
                           String productBrand, double productPrice, String productLink, String imageLink) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productBrand = productBrand;
        this.productPrice = Double.valueOf(productPrice);
        this.productLink = productLink;
        this.imageLink = imageLink;
    }

    // Getters and setters
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
    }    public Double getProductPrice() {
        return productPrice != null ? productPrice : 0.0;
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
