package com.shopsmart.shopsmart_server.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "deep_search_results")
public class DeepSearchResult {
    
    @Id
    private String id;
    
    private String userId;
    private String searchTerm;
    private String searchType; // "UPC" or "NAME"
    private List<ProductSearchItem> searchResults;
    private LocalDateTime createdAt;
    private Map<Integer, Double> combinedRatingPercentages; // Combined rating percentages across all domains
    
    // Default constructor
    public DeepSearchResult() {
        this.createdAt = LocalDateTime.now();
        this.combinedRatingPercentages = new HashMap<>();
    }
    
    // Constructor with fields
    public DeepSearchResult(String userId, String searchTerm, String searchType, List<ProductSearchItem> searchResults) {
        this.userId = userId;
        this.searchTerm = searchTerm;
        this.searchType = searchType;
        this.searchResults = searchResults;
        this.createdAt = LocalDateTime.now();
        this.combinedRatingPercentages = new HashMap<>();
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
    
    public List<ProductSearchItem> getSearchResults() {
        return searchResults;
    }
    
    public void setSearchResults(List<ProductSearchItem> searchResults) {
        this.searchResults = searchResults;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Map<Integer, Double> getCombinedRatingPercentages() {
        return combinedRatingPercentages;
    }
    
    public void setCombinedRatingPercentages(Map<Integer, Double> combinedRatingPercentages) {
        this.combinedRatingPercentages = combinedRatingPercentages;
    }
    
    // Static class for user reviews
    public static class UserReview {
        private String reviewText;
        private Integer rating; // Rating from 1-5
        
        // Default constructor
        public UserReview() {
        }
        
        // Constructor with fields
        public UserReview(String reviewText, Integer rating) {
            this.reviewText = reviewText;
            this.rating = rating;
        }
        
        // Getters and Setters
        public String getReviewText() {
            return reviewText;
        }
        
        public void setReviewText(String reviewText) {
            this.reviewText = reviewText;
        }
        
        public Integer getRating() {
            return rating;
        }
        
        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
    
    // Static nested class for product search items
    public static class ProductSearchItem {
        private String domain;
        private String productName;
        private String productUrl;
        private Double price;
        private Double rating;
        private Integer reviewCount;
        private Boolean inStock;
        private Map<Integer, Double> ratingPercentages; // Map of rating (1-5) to percentage
        private List<UserReview> userReviews; // List of up to 5 user reviews with ratings
        
        // Default constructor
        public ProductSearchItem() {
        }
        
        // Constructor with fields
        public ProductSearchItem(String domain, String productName, String productUrl, 
                              Double price, Double rating, Integer reviewCount, 
                              Boolean inStock, Map<Integer, Double> ratingPercentages,
                              List<UserReview> userReviews) {
            this.domain = domain;
            this.productName = productName;
            this.productUrl = productUrl;
            this.price = price;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.inStock = inStock;
            this.ratingPercentages = ratingPercentages;
            this.userReviews = userReviews;
        }
        
        // Legacy constructor for backward compatibility
        public ProductSearchItem(String domain, String productName, String productUrl, 
                              Double price, String currency, Double rating, 
                              Integer reviewCount, Boolean inStock) {
            this.domain = domain;
            this.productName = productName;
            this.productUrl = productUrl;
            this.price = price;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.inStock = inStock;
            this.ratingPercentages = new HashMap<>();
            this.userReviews = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getDomain() {
            return domain;
        }
        
        public void setDomain(String domain) {
            this.domain = domain;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public String getProductUrl() {
            return productUrl;
        }
        
        public void setProductUrl(String productUrl) {
            this.productUrl = productUrl;
        }
        
        public Double getPrice() {
            return price;
        }
        
        public void setPrice(Double price) {
            this.price = price;
        }
        
        public Double getRating() {
            return rating;
        }
        
        public void setRating(Double rating) {
            this.rating = rating;
        }
        
        public Integer getReviewCount() {
            return reviewCount;
        }
        
        public void setReviewCount(Integer reviewCount) {
            this.reviewCount = reviewCount;
        }
        
        public Boolean getInStock() {
            return inStock;
        }
        
        public void setInStock(Boolean inStock) {
            this.inStock = inStock;
        }
        
        public Map<Integer, Double> getRatingPercentages() {
            return ratingPercentages;
        }
        
        public void setRatingPercentages(Map<Integer, Double> ratingPercentages) {
            this.ratingPercentages = ratingPercentages;
        }
        
        public List<UserReview> getUserReviews() {
            return userReviews;
        }
        
        public void setUserReviews(List<UserReview> userReviews) {
            this.userReviews = userReviews;
        }
    }
}