package com.example.mobileshop.views;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobileshop.utils.SessionManager;

import com.example.mobileshop.api.RetrofitClient;
import com.example.mobileshop.api.ProductSearchService;
import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.ProductSearchRequest;
import com.example.mobileshop.models.ProductSearchResponse;
import com.example.mobileshop.models.ProductSearchResponse.ProductSearchResult;

import retrofit2.Call;
import retrofit2.Callback;
import com.example.mobileshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProductComparisonActivity extends AppCompatActivity {

    private static final String TAG = "ProductComparisonActivity";
    
    private LinearLayout vendorsContainer;
    private LinearLayout reviewsContainer;
    private TextView tvOverallRating;
    private TextView tvRatingBreakdown;
    private ProgressBar loadingProgressBar;
    
    private String productName;
    private String userId; // Will be retrieved from SessionManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_comparison_new);
        
        // Initialize views
        vendorsContainer = findViewById(R.id.vendorsContainer);
        reviewsContainer = findViewById(R.id.reviewsContainer);
        tvOverallRating = findViewById(R.id.tvOverallRating);
        tvRatingBreakdown = findViewById(R.id.tvRatingBreakdown);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        
        // Get user ID from session manager
        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        
        // If the user is not logged in or userID is null, use a default value
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "User ID not found in session, using default");
            userId = "6829021c4d26cf04aba961cc"; // Fallback to default user ID
        }
        
        Log.d(TAG, "Using user ID: " + userId);
        
        // Get product name from intent
        productName = getIntent().getStringExtra("productName");
        if (productName == null || productName.isEmpty()) {
            showError("Product information is missing");
            return;
        }
        
        // Set title
        setTitle("Compare: " + productName);
        
        // Check network connectivity
        if (!isNetworkAvailable()) {
            showError("No network connection available");
            return;
        }
        
        // Fetch comparison data
        fetchComparisonData();
    }
    
    private void fetchComparisonData() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        
        // Check network connectivity first
        if (!isNetworkAvailable()) {
            loadingProgressBar.setVisibility(View.GONE);
            showError("No internet connection available. Please check your network settings and try again.");
            return;
        }
        
        // Create API client
        ProductSearchService apiService = RetrofitClient.getClient().create(ProductSearchService.class);
        
        // Create request body
        ProductSearchRequest request = new ProductSearchRequest(productName, "NAME");
        
        Log.d(TAG, "Making API call to searchProducts endpoint");
        Log.d(TAG, "Request parameters: userId=" + userId + ", searchTerm=" + productName);
        Log.d(TAG, "API URL: " + RetrofitClient.getClient().baseUrl() + "api/search/" + userId + "/product-search");
        
        // Make API request
        apiService.searchProducts(userId, request).enqueue(new Callback<ApiResponse<ProductSearchResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductSearchResponse>> call, 
                                  retrofit2.Response<ApiResponse<ProductSearchResponse>> response) {
                loadingProgressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductSearchResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d(TAG, "API call successful: " + apiResponse.getMessage());
                        processRetrofitResponse(apiResponse.getData());
                    } else {
                        String errorMsg = apiResponse != null ? apiResponse.getMessage() : "Unknown error";
                        Log.e(TAG, "API returned error: " + errorMsg);
                        showError("API Error: " + errorMsg);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? 
                            response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "API call failed: " + errorBody);
                        showError("API Error: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "API call failed with code: " + response.code());
                        showError("API Error: Response code " + response.code());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<ProductSearchResponse>> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "API call failed with exception", t);
                Log.e(TAG, "Failed request URL: " + call.request().url());
                Log.e(TAG, "Failed request method: " + call.request().method());
                Log.e(TAG, "Request body: " + request.toString());
                showError("Network error: " + t.getMessage());
            }
        });
    }
    
    private void processComparisonResponse(JSONObject response) {
        try {
            Log.d(TAG, "API Response received: " + response.toString());
            
            // Check if search was successful
            boolean success = response.optBoolean("success", false);
            if (!success) {
                String message = response.optString("message", "Unknown error");
                showError("API Error: " + message);
                return;
            }
            
            // Get data object
            JSONObject dataObj = response.getJSONObject("data");
            
            // Check if we have search results
            if (!dataObj.has("searchResults")) {
                Log.w(TAG, "Response is missing searchResults array");
                TextView noResultsText = new TextView(this);
                noResultsText.setText("No comparison results found for " + productName);
                noResultsText.setPadding(16, 16, 16, 16);
                vendorsContainer.addView(noResultsText);
                return;
            }
            
            // Display vendor comparison
            JSONArray searchResults = dataObj.getJSONArray("searchResults");
            Log.d(TAG, "Found " + searchResults.length() + " vendor comparison results");
            displayVendorComparison(searchResults);
            
            // Display reviews if we have them
            if (dataObj.has("combinedRatingPercentages")) {
                displayCombinedRatings(dataObj.optJSONObject("combinedRatingPercentages"));
            } else {
                Log.d(TAG, "No ratings data found in the response");
                tvOverallRating.setText("No ratings available");
                tvRatingBreakdown.setVisibility(View.GONE);
            }
            
            // Display reviews
            displayReviews(searchResults);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error processing response", e);
            showError("Error processing response: " + e.getMessage());
        }
    }
    
    private void processRetrofitResponse(ProductSearchResponse response) {
        Log.d(TAG, "Processing Retrofit API response");
        
        if (response == null) {
            showError("Empty response from API");
            return;
        }
        
        // Check if we have search results
        List<ProductSearchResult> searchResults = response.getSearchResults();
        if (searchResults == null || searchResults.isEmpty()) {
            Log.w(TAG, "Response contains no search results");
            TextView noResultsText = new TextView(this);
            noResultsText.setText("No comparison results found for " + productName);
            noResultsText.setPadding(16, 16, 16, 16);
            vendorsContainer.addView(noResultsText);
            return;
        }
        
        // Log the number of results found
        Log.d(TAG, "Found " + searchResults.size() + " vendor comparison results");
        
        // Convert search results to JSONArray for the existing display methods
        try {
            JSONArray resultsArray = new JSONArray();
            for (ProductSearchResult result : searchResults) {
                JSONObject vendorObj = new JSONObject();
                
                // Add all the vendor details
                vendorObj.put("domain", result.getDomain());
                vendorObj.put("productName", result.getProductName());
                vendorObj.put("productUrl", result.getProductUrl());
                
                // Handle nullable fields
                if (result.getPrice() != null) {
                    vendorObj.put("price", result.getPrice());
                }
                
                if (result.getCurrency() != null) {
                    vendorObj.put("currency", result.getCurrency());
                }
                
                if (result.getRating() != null) {
                    vendorObj.put("rating", result.getRating());
                }
                
                if (result.getReviewCount() != null) {
                    vendorObj.put("reviewCount", result.getReviewCount());
                }
                
                if (result.getInStock() != null) {
                    vendorObj.put("inStock", result.getInStock());
                }
                
                resultsArray.put(vendorObj);
            }
            
            // Use existing methods to display the results
            displayVendorComparison(resultsArray);
            displayReviews(resultsArray);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error converting API response to JSON format", e);
            showError("Error processing API response: " + e.getMessage());
        }
    }
    
    private void displayCombinedRatings(JSONObject ratingPercentages) {
        if (ratingPercentages == null) {
            tvOverallRating.setText("No ratings available");
            return;
        }
        
        // Calculate overall rating
        double weightedSum = 0;
        double totalPercentage = 0;
        
        try {
            // Iterate through rating keys (1-5)
            for (int i = 1; i <= 5; i++) {
                if (ratingPercentages.has(String.valueOf(i))) {
                    double percentage = ratingPercentages.getDouble(String.valueOf(i));
                    weightedSum += i * percentage;
                    totalPercentage += percentage;
                }
            }
            
            // Calculate and display overall rating
            double overallRating = totalPercentage > 0 ? weightedSum / totalPercentage : 0;
            DecimalFormat df = new DecimalFormat("#.#");
            tvOverallRating.setText(df.format(overallRating) + " / 5.0");
            
            // Build rating breakdown text
            StringBuilder breakdownText = new StringBuilder();
            for (int i = 5; i >= 1; i--) {
                double percentage = ratingPercentages.optDouble(String.valueOf(i), 0.0);
                breakdownText.append(i).append(" stars: ").append((int)percentage).append("%\n");
            }
            
            tvRatingBreakdown.setText(breakdownText.toString().trim());
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing rating percentages", e);
            tvOverallRating.setText("Error displaying ratings");
        }
    }
    
    private void displayVendorComparison(JSONArray searchResults) {
        if (searchResults == null || searchResults.length() == 0) {
            TextView noVendorsText = new TextView(this);
            noVendorsText.setText("No vendor information available");
            vendorsContainer.addView(noVendorsText);
            return;
        }
        
        // Clear existing views
        vendorsContainer.removeAllViews();
        
        try {
            // Process each vendor
            for (int i = 0; i < searchResults.length(); i++) {
                JSONObject vendor = searchResults.getJSONObject(i);
                View vendorView = createVendorView(vendor);
                vendorsContainer.addView(vendorView);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error displaying vendor comparison", e);
            showError("Error displaying vendor comparison");
        }
    }
    
    private View createVendorView(JSONObject vendor) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View vendorView = inflater.inflate(R.layout.item_vendor_comparison, null);
        
        // Get views
        TextView tvVendorDomain = vendorView.findViewById(R.id.tvVendorDomain);
        TextView tvProductName = vendorView.findViewById(R.id.tvProductName);
        TextView tvProductPrice = vendorView.findViewById(R.id.tvProductPrice);
        RatingBar rbVendorRating = vendorView.findViewById(R.id.rbVendorRating);
        TextView tvInStock = vendorView.findViewById(R.id.tvInStock);
        
        // Set data
        tvVendorDomain.setText(vendor.optString("domain", "Unknown vendor"));
        tvProductName.setText(vendor.optString("productName", "Unknown product"));
        
        // Format price (bold)
        Double price = vendor.isNull("price") ? null : vendor.optDouble("price");
        if (price != null) {
            DecimalFormat df = new DecimalFormat("$#,##0.00");
            tvProductPrice.setText(df.format(price));
        } else {
            tvProductPrice.setText("Price unavailable");
        }
        
        // Display rating
        Float rating = vendor.isNull("rating") ? null : (float)vendor.optDouble("rating", 0.0);
        if (rating != null) {
            rbVendorRating.setRating(rating);
            rbVendorRating.setVisibility(View.VISIBLE);
        } else {
            rbVendorRating.setVisibility(View.GONE);
        }
        
        // Display stock status
        Boolean inStock = vendor.isNull("inStock") ? null : vendor.optBoolean("inStock");
        if (inStock != null) {
            tvInStock.setText(inStock ? "In Stock" : "Out of Stock");
            tvInStock.setTextColor(getResources().getColor(inStock ? 
                    android.R.color.holo_green_dark : android.R.color.holo_red_dark));
        } else {
            tvInStock.setText("Stock status unknown");
            tvInStock.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        return vendorView;
    }
    
    private void displayReviews(JSONArray searchResults) {
        if (searchResults == null || searchResults.length() == 0) {
            TextView noReviewsText = new TextView(this);
            noReviewsText.setText("No reviews available");
            reviewsContainer.addView(noReviewsText);
            return;
        }
        
        // Clear existing views
        reviewsContainer.removeAllViews();
        
        try {
            // Collect all reviews
            boolean hasReviews = false;
            
            for (int i = 0; i < searchResults.length(); i++) {
                JSONObject vendor = searchResults.getJSONObject(i);
                String domain = vendor.optString("domain", "Unknown");
                
                JSONArray userReviews = vendor.optJSONArray("userReviews");
                if (userReviews != null && userReviews.length() > 0) {
                    hasReviews = true;
                    
                    // Display reviews for this vendor
                    for (int j = 0; j < userReviews.length(); j++) {
                        JSONObject review = userReviews.getJSONObject(j);
                        View reviewView = createReviewView(review, domain);
                        reviewsContainer.addView(reviewView);
                    }
                }
            }
            
            if (!hasReviews) {
                TextView noReviewsText = new TextView(this);
                noReviewsText.setText("No reviews available");
                reviewsContainer.addView(noReviewsText);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error displaying reviews", e);
            showError("Error displaying reviews");
        }
    }
    
    private View createReviewView(JSONObject review, String domain) {
        CardView cardView = new CardView(this);
        cardView.setRadius(8);
        cardView.setCardElevation(4);
        cardView.setUseCompatPadding(true);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        
        // Rating bar
        RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyleSmall);
        ratingBar.setRating((float)review.optDouble("rating", 0.0));
        ratingBar.setIsIndicator(true);
        layout.addView(ratingBar);
        
        // Source text
        TextView sourceText = new TextView(this);
        sourceText.setText("Source: " + domain);
        sourceText.setTextSize(12);
        sourceText.setPadding(0, 8, 0, 8);
        layout.addView(sourceText);
        
        // Review text
        TextView reviewText = new TextView(this);
        reviewText.setText(review.optString("reviewText", "No review content"));
        reviewText.setPadding(0, 8, 0, 0);
        layout.addView(reviewText);
        
        cardView.addView(layout);
        return cardView;
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }
    
    // Check if the device has network connectivity
    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) 
            getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.net.Network network = connectivityManager.getActiveNetwork();
                android.net.NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && (
                    capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }
}
