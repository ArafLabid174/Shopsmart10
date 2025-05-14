package com.shopsmart.shopsmart_server.dto;

public class UpcLookupRequest {
    private String upcCode;
    
    // Default constructor
    public UpcLookupRequest() {
    }
    
    // Constructor with field
    public UpcLookupRequest(String upcCode) {
        this.upcCode = upcCode;
    }
    
    // Getter and Setter
    public String getUpcCode() {
        return upcCode;
    }
    
    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }
}