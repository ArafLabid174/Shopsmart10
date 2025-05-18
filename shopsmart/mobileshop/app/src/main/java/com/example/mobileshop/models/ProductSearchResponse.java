package com.example.mobileshop.models;

import java.util.List;

public class ProductSearchResponse {
    private String id;
    private String userId;
    private String searchTerm;
    private String searchType;
    private List<ProductSearchResult> searchResults;
    private List<Integer> createdAt;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public String getSearchType() {
        return searchType;
    }

    public List<ProductSearchResult> getSearchResults() {
        return searchResults;
    }

    public List<Integer> getCreatedAt() {
        return createdAt;
    }

    public static class ProductSearchResult {
        private String domain;
        private String productName;
        private String productUrl;
        private Double price;
        private String currency;
        private Float rating;
        private Integer reviewCount;
        private Boolean inStock;

        public String getDomain() {
            return domain;
        }

        public String getProductName() {
            return productName;
        }

        public String getProductUrl() {
            return productUrl;
        }

        public Double getPrice() {
            return price;
        }

        public String getCurrency() {
            return currency;
        }

        public Float getRating() {
            return rating;
        }

        public Integer getReviewCount() {
            return reviewCount;
        }

        public Boolean getInStock() {
            return inStock;
        }
    }
}
