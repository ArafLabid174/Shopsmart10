package com.example.mobileshop.models;

public class ProductLookupRequest {
    private String upcCode;

    public ProductLookupRequest(String upcCode) {
        this.upcCode = upcCode;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }
}