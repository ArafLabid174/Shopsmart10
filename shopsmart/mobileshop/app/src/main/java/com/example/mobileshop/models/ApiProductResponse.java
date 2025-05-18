package com.example.mobileshop.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model representing the full product API response including success status and message.
 */
public class ApiProductResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private ProductData data;
    
    /**
     * Data section containing total, offset, and item array
     */
    public static class ProductData {
        @SerializedName("code")
        private String code;
        
        @SerializedName("total")
        private int total;
        
        @SerializedName("offset")
        private int offset;
        
        @SerializedName("items")
        private List<ProductItem> items;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        public int getOffset() { return offset; }
        public void setOffset(int offset) { this.offset = offset; }

        public List<ProductItem> getItems() { return items; }
        public void setItems(List<ProductItem> items) { this.items = items; }
    }
    
    /**
     * Individual product item with details
     */
    public static class ProductItem {
        @SerializedName("ean")
        private String ean;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("upc")
        private String upc;
        
        @SerializedName("brand")
        private String brand;
        
        @SerializedName("model")
        private String model;
        
        @SerializedName("color")
        private String color;
        
        @SerializedName("size")
        private String size;
        
        @SerializedName("dimension")
        private String dimension;
        
        @SerializedName("weight")
        private String weight;
        
        @SerializedName("category")
        private String category;
        
        @SerializedName("currency")
        private String currency;
          @SerializedName("lowest_recorded_price")
        private Double lowestRecordedPrice;
        
        @SerializedName("highest_recorded_price")
        private Double highestRecordedPrice;
        
        @SerializedName("images")
        private List<String> images;
        
        @SerializedName("offers")
        private List<Offer> offers;

        public String getEan() { return ean; }
        public void setEan(String ean) { this.ean = ean; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getUpc() { return upc; }
        public void setUpc(String upc) { this.upc = upc; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getDimension() { return dimension; }
        public void setDimension(String dimension) { this.dimension = dimension; }

        public String getWeight() { return weight; }
        public void setWeight(String weight) { this.weight = weight; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }        public Double getLowestRecordedPrice() { return lowestRecordedPrice != null ? lowestRecordedPrice : 0.0; }
        public void setLowestRecordedPrice(Double lowestRecordedPrice) { this.lowestRecordedPrice = lowestRecordedPrice; }

        public Double getHighestRecordedPrice() { return highestRecordedPrice != null ? highestRecordedPrice : 0.0; }
        public void setHighestRecordedPrice(Double highestRecordedPrice) { this.highestRecordedPrice = highestRecordedPrice; }

        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }

        public List<Offer> getOffers() { return offers; }
        public void setOffers(List<Offer> offers) { this.offers = offers; }
    }
    
    /**
     * Product offer details
     */
    public static class Offer {
        @SerializedName("merchant")
        private String merchant;
        
        @SerializedName("domain")
        private String domain;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("currency")
        private String currency;
          @SerializedName("list_price")
        private Double listPrice;
        
        @SerializedName("price")
        private Double price;
        
        @SerializedName("shipping")
        private String shipping;
        
        @SerializedName("condition")
        private String condition;
        
        @SerializedName("availability")
        private String availability;
        
        @SerializedName("link")
        private String link;
        
        @SerializedName("updated_t")
        private long updatedTimestamp;

        public String getMerchant() { return merchant; }
        public void setMerchant(String merchant) { this.merchant = merchant; }

        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }        public Double getListPrice() { return listPrice != null ? listPrice : 0.0; }
        public void setListPrice(Double listPrice) { this.listPrice = listPrice; }

        public Double getPrice() { return price != null ? price : 0.0; }
        public void setPrice(Double price) { this.price = price; }

        public String getShipping() { return shipping; }
        public void setShipping(String shipping) { this.shipping = shipping; }

        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }

        public String getAvailability() { return availability; }
        public void setAvailability(String availability) { this.availability = availability; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }

        public long getUpdatedTimestamp() { return updatedTimestamp; }
        public void setUpdatedTimestamp(long updatedTimestamp) { this.updatedTimestamp = updatedTimestamp; }
    }
    
    // Getters and setters for the main class
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ProductData getData() { return data; }
    public void setData(ProductData data) { this.data = data; }
    
    /**
     * Create a sample fallback response for when product lookup fails
     * @param upcCode The UPC code that was scanned
     * @return A sample ApiProductResponse with placeholder data
     */
    public static ApiProductResponse createSampleResponse(String upcCode) {
        ApiProductResponse response = new ApiProductResponse();
        response.setSuccess(true);
        response.setMessage("Sample product data");
        
        ProductData productData = new ProductData();
        productData.setCode("OK");
        productData.setTotal(1);
        productData.setOffset(0);
        
        ProductItem item = new ProductItem();
        item.setTitle("Sample Product #" + upcCode);
        item.setDescription("This is a sample product description. " +
                "The actual product details could not be retrieved from the database. " +
                "This placeholder is shown to demonstrate the app's functionality.");
        item.setUpc(upcCode);
        item.setEan(upcCode);
        item.setBrand("Generic Brand");
        item.setModel("SMP-" + upcCode.substring(Math.max(0, upcCode.length() - 4)));
        item.setColor("Various");
        item.setWeight("N/A");
        item.setCategory("Miscellaneous > Sample Products");
          // Set some reasonable price values
        item.setLowestRecordedPrice(Double.valueOf(19.99));
        item.setHighestRecordedPrice(Double.valueOf(29.99));
        
        // Add a neutral placeholder image URL
        java.util.ArrayList<String> images = new java.util.ArrayList<>();
        images.add("https://placehold.co/400x400?text=Product+Image");
        item.setImages(images);
        
        // Add a sample offer
        Offer offer = new Offer();
        offer.setMerchant("Sample Store");
        offer.setDomain("samplestore.com");
        offer.setTitle("Sample Product");
        offer.setCurrency("$");
        offer.setListPrice(Double.valueOf(24.99));
        offer.setPrice(Double.valueOf(19.99));
        offer.setShipping("Standard Shipping");
        offer.setCondition("New");
        offer.setAvailability("In Stock");
        offer.setLink("https://example.com/product");
        offer.setUpdatedTimestamp(System.currentTimeMillis() / 1000);
        
        java.util.ArrayList<Offer> offers = new java.util.ArrayList<>();
        offers.add(offer);
        item.setOffers(offers);
        
        java.util.ArrayList<ProductItem> items = new java.util.ArrayList<>();
        items.add(item);
        productData.setItems(items);
        
        response.setData(productData);
        
        return response;
    }
}