package com.example.mobileshop.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductLookupResponse {
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("upcCode")
    private String upcCode;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("price")
    private PriceInfo price;
    
    @SerializedName("features")
    private List<String> features;
    
    @SerializedName("tipsAndAdvice")
    private String tipsAndAdvice;
    
    @SerializedName("reviews")
    private ReviewInfo reviews;

    // Price information
    public static class PriceInfo {
        @SerializedName("lowest")
        private String lowest;
        
        @SerializedName("highest")
        private String highest;
        
        @SerializedName("average")
        private String average;

        public String getLowest() { return lowest; }
        public void setLowest(String lowest) { this.lowest = lowest; }

        public String getHighest() { return highest; }
        public void setHighest(String highest) { this.highest = highest; }

        public String getAverage() { return average; }
        public void setAverage(String average) { this.average = average; }
    }

    // Review information
    public static class ReviewInfo {
        @SerializedName("rating")
        private double rating;
        
        @SerializedName("totalReviews")
        private int totalReviews;
        
        @SerializedName("reviewList")
        private List<Review> reviewList;

        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }

        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

        public List<Review> getReviewList() { return reviewList; }
        public void setReviewList(List<Review> reviewList) { this.reviewList = reviewList; }
    }

    // Individual review
    public static class Review {
        @SerializedName("author")
        private String author;
        
        @SerializedName("rating")
        private int rating;
        
        @SerializedName("comment")
        private String comment;
        
        @SerializedName("date")
        private String date;

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }

    // Getters and Setters for main class
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUpcCode() { return upcCode; }
    public void setUpcCode(String upcCode) { this.upcCode = upcCode; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public PriceInfo getPrice() { return price; }
    public void setPrice(PriceInfo price) { this.price = price; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public String getTipsAndAdvice() { return tipsAndAdvice; }
    public void setTipsAndAdvice(String tipsAndAdvice) { this.tipsAndAdvice = tipsAndAdvice; }

    public ReviewInfo getReviews() { return reviews; }
    public void setReviews(ReviewInfo reviews) { this.reviews = reviews; }
}