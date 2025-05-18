package com.example.mobileshop.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ProductHistoryResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ProductHistory> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<ProductHistory> getData() {
        return data;
    }

    public static class ProductHistory {
        @SerializedName("id")
        private String id;

        @SerializedName("userId")
        private String userId;

        @SerializedName("upcCode")
        private String upcCode;

        @SerializedName("lookupUrl")
        private String lookupUrl;

        @SerializedName("productTitle")
        private String productTitle;

        @SerializedName("productDescription")
        private String productDescription;

        @SerializedName("productBrand")
        private String productBrand;        @SerializedName("productPrice")
        private Double productPrice;

        @SerializedName("productLink")
        private String productLink;

        @SerializedName("imageLink")
        private String imageLink;

        @SerializedName("createdAt")
        private List<Integer> createdAt;

        public String getId() {
            return id;
        }

        public String getUserId() {
            return userId;
        }

        public String getUpcCode() {
            return upcCode;
        }

        public String getLookupUrl() {
            return lookupUrl;
        }

        public String getProductTitle() {
            return productTitle;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public String getProductBrand() {
            return productBrand;
        }        public double getProductPrice() {
            return productPrice != null ? productPrice : 0.0;
        }

        public String getProductLink() {
            return productLink;
        }

        public String getImageLink() {
            return imageLink;
        }

        public List<Integer> getCreatedAt() {
            return createdAt;
        }

        public String getFormattedDate() {
            if (createdAt != null && createdAt.size() >= 3) {
                return String.format("%d-%02d-%02d", createdAt.get(0), createdAt.get(1), createdAt.get(2));
            }
            return "Unknown date";
        }
    }
}
