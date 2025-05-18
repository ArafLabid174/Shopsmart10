package com.example.mobileshop.models;

public class ProductSearchRequest {
    private String searchTerm;
    private String searchType;

    public ProductSearchRequest(String searchTerm, String searchType) {
        this.searchTerm = searchTerm;
        this.searchType = searchType;
    }

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
}
