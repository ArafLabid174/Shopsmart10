package com.shopsmart.shopsmart_server.dto;

import java.util.List;

public class DeepSearchRequest {
    private String searchTerm;
    private String searchType; // "UPC" or "NAME"
    private List<String> domains; // Optional list of domains to search
    
    // Default constructor
    public DeepSearchRequest() {
    }
    
    // Constructor with fields
    public DeepSearchRequest(String searchTerm, String searchType, List<String> domains) {
        this.searchTerm = searchTerm;
        this.searchType = searchType;
        this.domains = domains;
    }
    
    // Getters and Setters
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public String getSearchType() {
        return searchType;
    }
    
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
    
    public List<String> getDomains() {
        return domains;
    }
    
    public void setDomains(List<String> domains) {
        this.domains = domains;
    }
}