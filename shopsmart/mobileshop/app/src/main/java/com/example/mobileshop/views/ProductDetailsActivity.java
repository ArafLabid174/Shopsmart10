package com.example.mobileshop.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import com.example.mobileshop.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductCode, tvProductPrice;
    private TextView tvFeaturesTitle, tvFeaturesList, tvTipsTitle, tvTipsContent;
    private TextView tvReviewsTitle, tvReviewsSummary;
    private TextView tvCategoryBadge, tvProductBrand, tvProductColor, tvProductWeight;
    private Button btnCompare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvDetailsProductName);
        tvProductCode = findViewById(R.id.tvDetailsProductCode);
        tvProductPrice = findViewById(R.id.tvDetailsProductPrice);
        tvFeaturesTitle = findViewById(R.id.tvFeaturesTitle);
        tvFeaturesList = findViewById(R.id.tvFeaturesList);
        tvTipsTitle = findViewById(R.id.tvTipsTitle);
        tvTipsContent = findViewById(R.id.tvTipsContent);
        tvReviewsTitle = findViewById(R.id.tvReviewsTitle);
        tvReviewsSummary = findViewById(R.id.tvReviewsSummary);
        tvCategoryBadge = findViewById(R.id.tvCategoryBadge);
        tvProductBrand = findViewById(R.id.tvProductBrand);
        tvProductColor = findViewById(R.id.tvProductColor);
        tvProductWeight = findViewById(R.id.tvProductWeight);
        btnCompare = findViewById(R.id.btnCompare);        // Retrieve the product JSON from intent
        String productJsonStr = getIntent().getStringExtra("productJson");
        Log.d("ProductDetailsActivity", "Received product JSON: " + productJsonStr);
        
        if (productJsonStr != null) {
            try {                // Log the full JSON for debugging
                Log.d("ProductDetailsActivity", "Parsing JSON: " + productJsonStr);
                JSONObject responseObj = new JSONObject(productJsonStr);
                
                // Check if this is the API format (with success field) or the legacy format (with name and code fields)
                // Get the data object that contains the items array
                JSONObject dataObj = responseObj.optJSONObject("data");
                Log.d("ProductDetailsActivity", "Data object: " + (dataObj != null ? "found" : "null"));

                if (dataObj != null && dataObj.has("items")) {
                    Log.d("ProductDetailsActivity", "Items array exists: " + dataObj.has("items"));
                    if (dataObj.has("items") && dataObj.getJSONArray("items").length() > 0) {
                        Log.d("ProductDetailsActivity", "Items array length: " + dataObj.getJSONArray("items").length());                                
                        // Get the first item from the items array (assuming that's our product)
                        JSONObject productObj = dataObj.getJSONArray("items").getJSONObject(0);
                        Log.d("ProductDetailsActivity", "Processing API format item: " + productObj.toString());
                        
                        // Extract product details
                        String title = productObj.optString("title", "Unknown Product");
                        String description = productObj.optString("description", "");
                        String ean = productObj.optString("ean", "");
                        String upc = productObj.optString("upc", "");
                        String category = productObj.optString("category", "Other");
                        String brand = productObj.optString("brand", "Not specified");
                        String color = productObj.optString("color", "Not specified");
                        String weight = productObj.optString("weight", "Not specified");
                        
                        // Provide a default description if none exists
                        if (description == null || description.trim().isEmpty()) {
                            description = "No description available for this product. " +
                                    "This " + brand + " product is designed to meet your needs with quality and reliability.";
                        }
                        
                        // Use either EAN or UPC as product code (prefer EAN if available)
                        String code = !ean.isEmpty() ? ean : upc;
                        
                        // Get the first image from the images array
                        String imageUrl = "";
                        if (productObj.has("images") && productObj.getJSONArray("images").length() > 0) {
                            imageUrl = productObj.getJSONArray("images").getString(0);
                        }
                        
                        // Get price information from the offers array
                        double price = 0.0;
                        String formattedPrice = "";
                        String currency = "";
                        
                        if (productObj.has("offers") && productObj.getJSONArray("offers").length() > 0) {
                            JSONObject firstOffer = productObj.getJSONArray("offers").getJSONObject(0);
                            // Get price with safe parsing
                            String priceStr = firstOffer.optString("price", "");
                            try {
                                if (priceStr != null && !priceStr.isEmpty()) {
                                    price = Double.parseDouble(priceStr);
                                }
                            } catch (NumberFormatException e) {
                                Log.e("ProductDetailsActivity", "Error parsing price: " + priceStr, e);
                            }
                            currency = firstOffer.optString("currency", "$");
                            formattedPrice = price > 0 ? String.format("Price: %s%.1f", currency, price) : "Price unavailable";
                        } else {
                            formattedPrice = "Price unavailable";
                        }
                        
                        // Display category badge (extract last part for simpler display)
                        String shortCategory = category;
                        if (category.contains(">")) {
                            String[] parts = category.split(">");
                            shortCategory = parts[parts.length - 1].trim();
                        }
                        tvCategoryBadge.setText(shortCategory);
                        tvCategoryBadge.setVisibility(View.VISIBLE);
                        
                        // Set text fields
                        tvProductName.setText(title);
                        tvProductCode.setText("Code: " + code);
                        tvProductPrice.setText(formattedPrice);
                        
                        // Set product attributes
                        tvProductBrand.setText("Brand: " + brand);
                        tvProductColor.setText("Color: " + color);
                        tvProductWeight.setText("Weight: " + weight);
                        
                        // Show description in the features section
                        tvFeaturesTitle.setText("Description");
                        tvFeaturesTitle.setVisibility(View.VISIBLE);
                        tvFeaturesList.setVisibility(View.VISIBLE);
                        tvFeaturesList.setText(description);
                        
                        // Hide tips and reviews sections for this implementation
                        tvTipsTitle.setVisibility(View.GONE);
                        tvTipsContent.setVisibility(View.GONE);
                        tvReviewsTitle.setVisibility(View.GONE);
                        tvReviewsSummary.setVisibility(View.GONE);
                        
                        // Load the product image
                        if (!imageUrl.isEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_background)
                                .into(ivProductImage);
                            Log.d("ProductDetailsActivity", "Loaded image from URL: " + imageUrl);
                        } else {
                            // Use a default image if none is provided
                            Glide.with(this)
                                .load(R.drawable.ic_launcher_foreground)
                                .into(ivProductImage);
                        }
                        
                        // Make sure the compare button is visible
                        btnCompare.setVisibility(View.VISIBLE);
                    } else {
                        showErrorState("No product items found in response");
                    }
                } else {
                    // For direct product JSON or legacy format
                    String name = responseObj.optString("name", "Unknown Product");
                    String code = responseObj.optString("code", "N/A");
                    
                    // Get the nested product object with details or use the responseObj directly
                    JSONObject detailsObj = responseObj.optJSONObject("product");
                    if (detailsObj == null) detailsObj = responseObj;  // Use main object if no nested product
                    
                    // Process product details
                    processLegacyProductFormat(name, code, detailsObj);
                }
            } catch (JSONException e) {
                Log.e("ProductDetailsActivity", "Error parsing product JSON", e);
                e.printStackTrace();
                
                // Show error UI
                showErrorState("Error loading product details");
            }
        } else {
            // No JSON data received
            showErrorState("No product data available");
        }
          // Handle "Compare" button
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open comparison screen
                Intent intent = new Intent(ProductDetailsActivity.this, ProductComparisonActivity.class);
                // Pass product name for the API search
                intent.putExtra("productName", tvProductName.getText().toString());
                startActivity(intent);
            }
        });
    }
      // Process the legacy format JSON from ScanFragment
    private void processLegacyProductFormat(String name, String code, JSONObject detailsObj) {
        try {
            Log.d("ProductDetailsActivity", "Processing legacy format product: " + name);
              // Extract image URL
            String imageUrl = detailsObj.optString("image", "");
            Log.d("Details data : ", detailsObj.toString());
            
            // Extract category and additional details if available
            String category = detailsObj.optString("category", "Other");
            String brand = detailsObj.optString("brand", "Not specified");
            String color = detailsObj.optString("color", "Not specified");
            String weight = detailsObj.optString("weight", "Not specified");
            
            // Extract price information
            JSONObject priceObj = detailsObj.optJSONObject("price");
            String priceText = "Price unavailable";
            
            if (priceObj != null) {
                String lowestPrice = priceObj.optString("lowest", "");
                String highestPrice = priceObj.optString("highest", "");
                String originalPrice = priceObj.optString("original", "");
                
                // Show price range if both lowest and highest are available
                if (!lowestPrice.isEmpty() && !highestPrice.isEmpty()) {
                    try {
                        double lowest = Double.parseDouble(lowestPrice.replaceAll("[^\\d.]", ""));
                        double highest = Double.parseDouble(highestPrice.replaceAll("[^\\d.]", ""));
                        String formattedLowest = String.format("%.3f", lowest);
                        String formattedHighest = String.format("%.3f", highest);
                        priceText = String.format("Price: %s - %s", formattedLowest, highestPrice);
                    } catch (NumberFormatException e) {
                        priceText = String.format("Price: %s - %s", lowestPrice, highestPrice);
                    }
                } else if (!originalPrice.isEmpty()) {
                    // Fall back to original price if range not available
                    priceText = "Price: " + originalPrice;
                }
            }
            
            // Extract features and tips
            StringBuilder featuresText = new StringBuilder();
            String tipsText = "No tips provided";
            
            JSONObject sectionsObj = detailsObj.optJSONObject("sections");
            if (sectionsObj != null) {
                // Process features
                if (sectionsObj.has("features")) {
                    JSONArray features = sectionsObj.getJSONArray("features");
                    for (int i = 0; i < features.length(); i++) {
                        featuresText.append("• ")
                                    .append(features.getString(i))
                                    .append("\n");
                    }
                }
                
                // Process tips
                tipsText = sectionsObj.optString("tips_and_advice", tipsText);
            }
              // Display category badge (extract last part for simpler display)
            String shortCategory = category;
            if (category.contains(">")) {
                String[] parts = category.split(">");
                shortCategory = parts[parts.length - 1].trim();
            }
            tvCategoryBadge.setText(shortCategory);
            tvCategoryBadge.setVisibility(View.VISIBLE);
            
            // Set text fields
            tvProductName.setText(name);
            tvProductCode.setText("Code: " + code);
            tvProductPrice.setText(priceText);
            
            // Set product attributes
            tvProductBrand.setText("Brand: " + brand);
            tvProductColor.setText("Color: " + color);
            tvProductWeight.setText("Weight: " + weight);
            
            // Set features and tips
            tvFeaturesTitle.setVisibility(View.VISIBLE);
            tvFeaturesList.setVisibility(View.VISIBLE);
            tvFeaturesList.setText(featuresText.toString().trim());
            
            tvTipsTitle.setVisibility(View.VISIBLE);
            tvTipsContent.setVisibility(View.VISIBLE);
            tvTipsContent.setText(tipsText);
            
            // Check for reviews
            JSONObject reviewsObj = detailsObj.optJSONObject("reviews");
            if (reviewsObj != null) {
                double rating = reviewsObj.optDouble("rating", 0);
                int totalReviews = reviewsObj.optInt("total_reviews", 0);
                
                tvReviewsTitle.setVisibility(View.VISIBLE);
                tvReviewsSummary.setVisibility(View.VISIBLE);
                tvReviewsSummary.setText(
                    rating + " out of 5 (" + totalReviews + " reviews)"
                );
            } else {
                tvReviewsTitle.setVisibility(View.GONE);
                tvReviewsSummary.setVisibility(View.GONE);
            }
            
            // Load the product image
            if (!imageUrl.isEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage);
            } else {
                // Use a default image if none is provided
                Glide.with(this)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(ivProductImage);
            }
            
            // Make sure the compare button is visible
            btnCompare.setVisibility(View.VISIBLE);
            
        } catch (JSONException e) {
            Log.e("ProductDetailsActivity", "Error processing legacy product format", e);
            showErrorState("Error processing product details");
        }
    }    // Show error UI when product details can't be loaded
    private void showErrorState(String errorMessage) {
        // Hide product details sections
        tvFeaturesTitle.setVisibility(View.GONE);
        tvFeaturesList.setVisibility(View.GONE);
        tvTipsTitle.setVisibility(View.GONE);
        tvTipsContent.setVisibility(View.GONE);
        tvReviewsTitle.setVisibility(View.GONE);
        tvReviewsSummary.setVisibility(View.GONE);
        
        // Hide category badge and additional details
        tvCategoryBadge.setVisibility(View.GONE);
        tvProductBrand.setVisibility(View.GONE);
        tvProductColor.setVisibility(View.GONE);
        tvProductWeight.setVisibility(View.GONE);
        
        // Show error message in product name field
        tvProductName.setText(errorMessage);
        tvProductCode.setText("");
        tvProductPrice.setText("");
        
        // Show placeholder image
        Glide.with(this)
            .load(R.drawable.ic_launcher_foreground)
            .into(ivProductImage);
            
        // Hide compare button
        btnCompare.setVisibility(View.GONE);
    }
}
