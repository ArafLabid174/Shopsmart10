package com.example.mobileshop.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileshop.R;
import com.example.mobileshop.api.CartApiService;
import com.example.mobileshop.api.ProductApiService;
import com.example.mobileshop.api.RetrofitClient;
import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.AddToCartRequest;
import com.example.mobileshop.models.ProductLookupRequest;
import com.example.mobileshop.models.ProductLookupResponse;
import com.example.mobileshop.models.ProductHistoryResponse;
import com.example.mobileshop.models.ApiProductResponse;
import com.example.mobileshop.utils.SessionManager;
import com.example.mobileshop.views.ProductDetailsActivity;
import com.example.mobileshop.views.ScanOptionsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;    public class ScanFragment extends Fragment {

    private RecyclerView recyclerView;
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh the product history when coming back to this fragment
        if (adapter != null) {
            loadProductHistory();
        }
    }
    private TextView tvNoProducts;
    private Button btnScanNow;
    private ArrayList<Product> productList = new ArrayList<>();
    private ProductAdapter adapter;

    private String dummyJson;
    private JSONArray originalProductsJson;

    // Random products list
    private String randomProducts = "[{\"name\":\"Product F\"}, {\"name\":\"Product G\"}, {\"name\":\"Product H\"}]";

    public ScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        btnScanNow = view.findViewById(R.id.btnScanNow);
        tvNoProducts = view.findViewById(R.id.tvNoProducts);
        recyclerView = view.findViewById(R.id.rvScannedProducts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);

        // "Scan Now" button
        btnScanNow.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScanOptionsActivity.class);
            startActivityForResult(intent, 100);
        });

        // Load JSON from file
        dummyJson = readJsonFromResource();
        try {
            originalProductsJson = new JSONArray(dummyJson);
        } catch (JSONException e) {
            e.printStackTrace();
            originalProductsJson = new JSONArray();
        }

        // Load product history from API
        loadProductHistory();

        return view;
    }

    // Read JSON from drawable/datas.json (though typically you'd use res/raw)
    private String readJsonFromResource() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.datas);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    // Load product history from API
    private void loadProductHistory() {
        // Show a progress dialog
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Loading product history...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Get user ID from session manager
        SessionManager sessionManager = new SessionManager(getContext());
        String userId = sessionManager.getUserId();
        
        // If the user is not logged in or userID is null, use a default value
        if (userId == null || userId.isEmpty()) {
            Log.w("ScanFragment", "User ID not found in session, using default");
            userId = "6828f6f94d26cf04aba961ca"; // Fallback to default user ID
        }
        
        // Use the userId variable in the API call
        final String finalUserId = userId; // Make userId final for use in callback
        
        // Create API service and make the call
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        apiService.getProductHistory(finalUserId).enqueue(new retrofit2.Callback<ProductHistoryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ProductHistoryResponse> call, retrofit2.Response<ProductHistoryResponse> response) {
                // Dismiss progress dialog
                progressDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // API call successful - display product history
                    Log.d("ScanFragment", "API history call successful. Items: " + response.body().getData().size());
                    
                    // Clear the current list and populate with the API response
                    productList.clear();
                    for (ProductHistoryResponse.ProductHistory product : response.body().getData()) {
                        try {
                            // Create a JSON object for the product details
                            JSONObject detailsObj = new JSONObject();
                            detailsObj.put("image", product.getImageLink());
                            
                            // Create a price object
                            JSONObject priceObj = new JSONObject();
                            double price = 0.0;
                            try {
                                // Get the product price with safe parsing
                                price = product.getProductPrice();
                                if (price > 0) {
                                    priceObj.put("lowest", price * 0.9); // Estimate lowest price
                                    priceObj.put("highest", price * 1.1); // Estimate highest price
                                    priceObj.put("original", price);
                                } else {
                                    priceObj.put("lowest", "");
                                    priceObj.put("highest", "");
                                    priceObj.put("original", "");
                                }
                            } catch (Exception e) {
                                Log.e("ScanFragment", "Error calculating price values", e);
                                priceObj.put("lowest", "");
                                priceObj.put("highest", "");
                                priceObj.put("original", "");
                            }
                            detailsObj.put("price", priceObj);
                            
                            // Create product object with history data
                            String productName = product.getProductTitle();
                            String upcCode = product.getUpcCode();
                            int id = productList.size() + 1;
                            
                            productList.add(new Product(id, productName, upcCode, detailsObj.toString()));
                            
                        } catch (JSONException e) {
                            Log.e("ScanFragment", "Error creating product JSON from history", e);
                        }
                    }
                    
                    // Update the RecyclerView
                    adapter.notifyDataSetChanged();
                    updateView();
                    
                } else {
                    // API call failed - fallback to dummy data
                    Log.e("ScanFragment", "API history call failed: " + 
                          (response.errorBody() != null ? response.errorBody().toString() : "No error body"));
                    loadDummyDataFallback();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ProductHistoryResponse> call, Throwable t) {
                // Dismiss progress dialog
                progressDialog.dismiss();
                
                // Network failure - show error and fallback to dummy data
                Log.e("ScanFragment", "API history call failed with exception", t);
                Toast.makeText(getContext(), 
                    "Network error: " + t.getMessage() + ". Using sample data.", 
                    Toast.LENGTH_SHORT).show();
                loadDummyDataFallback();
            }
        });
    }

    // Fallback method to load dummy data when API fails
    private void loadDummyDataFallback() {
        productList.clear();
        try {
            for (int i = 0; i < originalProductsJson.length(); i++) {
                JSONObject obj = originalProductsJson.getJSONObject(i);
                int id = obj.optInt("id", i + 1);
                String name = obj.optString("name", "Unnamed Product");
                String code = obj.optString("code", "N/A");
                String details = obj.has("product") ? obj.getJSONObject("product").toString() : "";

                String nameWithCode = name + " - " + code;
                productList.add(new Product(id, nameWithCode, code, details));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateView();
    }

    // Handle result from ScanOptionsActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            String scannedCode = data.getStringExtra("scannedCode");
            if (scannedCode != null) {
                // Log the extracted UPC code from the barcode scan
                Log.d("ScanFragment", "Extracted UPC code from barcode scan: " + scannedCode);
                lookupProductByBarcode(scannedCode);
            }
        }
    }
    
    // Call API to lookup product by barcode
    private void lookupProductByBarcode(String upcCode) {
        // Log the UPC code being looked up
        Log.d("ScanFragment", "Looking up product for UPC code: " + upcCode);
        // Show a progress dialog
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Looking up product information...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        ProductApiService apiService = RetrofitClient.getClient().create(ProductApiService.class);
        ProductLookupRequest request = new ProductLookupRequest(upcCode);
        
        // Get user ID from session manager instead of hardcoding it
        SessionManager sessionManager = new SessionManager(getContext());
        String userId = sessionManager.getUserId();
        
        // If the user is not logged in or userID is null, use a default value
        if (userId == null || userId.isEmpty()) {
            Log.w("ScanFragment", "User ID not found in session, using default");
            userId = "6828f6f94d26cf04aba961ca"; // Fallback to default user ID
            Toast.makeText(getContext(), "Using guest account for product lookup", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("ScanFragment", "Using authenticated user ID: " + userId);
        }
        
        Log.d("ScanFragment", "Making API call for barcode: " + upcCode + " with user ID: " + userId);
        
        // Use the userId variable in the API call
        final String finalUserId = userId; // Make userId final for use in callback
        
        // Log detailed connection information 
        Log.d("ScanFragment", "Connecting to server: " + RetrofitClient.getClient().baseUrl().toString());
        Log.d("ScanFragment", "Network check: " + isNetworkAvailable());
        
        apiService.lookupProduct(finalUserId, request).enqueue(new retrofit2.Callback<ApiProductResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiProductResponse> call, retrofit2.Response<ApiProductResponse> response) {
                // Dismiss progress dialog
                progressDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiProductResponse apiResponse = response.body();
                    Log.d("ScanFragment", "API response: " + apiResponse.toString());
                    // Check if the API response is successful
                    if (apiResponse.isSuccess() && apiResponse.getData() != null && 
                        apiResponse.getData().getItems() != null && !apiResponse.getData().getItems().isEmpty()) {
                        
                        // API call successful - display product details
                        Log.d("ScanFragment", "API call successful. Response: " + apiResponse.getMessage());
                        
                        // Convert the API product response to JSON string for ProductDetailsActivity
                        try {
                            // Convert API response to JSON string
                            String jsonString = new com.google.gson.Gson().toJson(apiResponse);
                            Log.d("ScanFragment", "JSON string to pass to ProductDetailsActivity length: " + jsonString.length());
                            
                            // Use the debug utility
                            Log.d("ScanFragment", "Debugging API response JSON before sending to ProductDetailsActivity");
                            com.example.mobileshop.utils.ProductJsonDebugger.analyze(jsonString);
                            
                            // Create intent and pass the JSON
                            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                            intent.putExtra("productJson", jsonString);
                            startActivity(intent);
                            
                            // Also add to product history list for view
                            ApiProductResponse.ProductItem item = apiResponse.getData().getItems().get(0);
                            String productName = item.getTitle();
                            if (productName == null || productName.isEmpty()) {
                                productName = "Unknown Product";
                            }
                            
                            productList.add(new Product(productList.size() + 1, 
                                productName, upcCode, new com.google.gson.Gson().toJson(item)));
                            adapter.notifyItemInserted(productList.size() - 1);
                            updateView();
                            
                        } catch (Exception e) {
                            Log.e("ScanFragment", "Error converting API response to JSON", e);
                            Toast.makeText(getContext(), "Error processing product data", Toast.LENGTH_SHORT).show();
                            addScannedProduct(upcCode);
                        }
                    } else {
                        // API returned success=false or no items
                        Log.e("ScanFragment", "API call returned success=false or no items: " + 
                             apiResponse.getMessage());
                        Toast.makeText(getContext(), 
                            "No product found: " + apiResponse.getMessage() + ". Using sample data.", 
                            Toast.LENGTH_SHORT).show();
                            
                        // Use sample response with placeholder data instead of random data
                        ApiProductResponse sampleResponse = ApiProductResponse.createSampleResponse(upcCode);
                        
                        // Convert the sample response to JSON string
                        try {
                            String jsonString = new com.google.gson.Gson().toJson(sampleResponse);
                            Log.d("ScanFragment", "Using sample data JSON: " + jsonString.substring(0, Math.min(100, jsonString.length())) + "...");
                            
                            // Create intent and pass the JSON
                            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                            intent.putExtra("productJson", jsonString);
                            startActivity(intent);
                            
                            // Also add to product history list for view
                            ApiProductResponse.ProductItem item = sampleResponse.getData().getItems().get(0);
                            productList.add(new Product(productList.size() + 1, 
                                item.getTitle(), upcCode, new com.google.gson.Gson().toJson(item)));
                            adapter.notifyItemInserted(productList.size() - 1);
                            updateView();
                        } catch (Exception e) {
                            Log.e("ScanFragment", "Error creating sample data", e);
                            // Fall back to the old method if anything goes wrong
                            addScannedProduct(upcCode);
                        }
                    }
                } else {
                    // API call failed - show error and fallback to sample product
                    Log.e("ScanFragment", "API call failed with code: " + response.code() + 
                        ", Message: " + (response.errorBody() != null ? response.errorBody().toString() : "No error body") +
                        ", User ID: " + finalUserId + ", UPC: " + upcCode);
                    Toast.makeText(getContext(), "Failed to lookup product. Using sample data.", Toast.LENGTH_SHORT).show();
                    
                    // Use sample response with placeholder data
                    ApiProductResponse sampleResponse = ApiProductResponse.createSampleResponse(upcCode);
                    String jsonString = new com.google.gson.Gson().toJson(sampleResponse);
                    
                    try {
                        // Create intent and pass the JSON
                        Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                        intent.putExtra("productJson", jsonString);
                        startActivity(intent);
                        
                        // Also add to product history list for view
                        ApiProductResponse.ProductItem item = sampleResponse.getData().getItems().get(0);
                        productList.add(new Product(productList.size() + 1, 
                            item.getTitle(), upcCode, new com.google.gson.Gson().toJson(item)));
                        adapter.notifyItemInserted(productList.size() - 1);
                        updateView();
                    } catch (Exception e) {
                        Log.e("ScanFragment", "Error using sample data", e);
                        // Fall back to the old method if anything goes wrong
                        addScannedProduct(upcCode);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiProductResponse> call, Throwable t) {
                // Dismiss progress dialog
                progressDialog.dismiss();
                
                // Network failure - show error and fallback to sample product
                Log.e("ScanFragment", "API call failed with exception", t);
                Log.e("ScanFragment", "Failed API request details - User ID: " + finalUserId + ", UPC: " + upcCode);
                Toast.makeText(getContext(), 
                    "Network error: " + t.getMessage() + ". Using sample data.", 
                    Toast.LENGTH_SHORT).show();
                    
                try {
                    // Use sample response with placeholder data
                    ApiProductResponse sampleResponse = ApiProductResponse.createSampleResponse(upcCode);
                    String jsonString = new com.google.gson.Gson().toJson(sampleResponse);
                    
                    // Log the sample data
                    Log.d("ScanFragment", "Generated sample data JSON for network failure: " + 
                          jsonString.substring(0, Math.min(100, jsonString.length())) + "...");
                    
                    // Create intent and pass the JSON
                    Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                    intent.putExtra("productJson", jsonString);
                    startActivity(intent);
                    
                    // Also add to product history list for view
                    ApiProductResponse.ProductItem item = sampleResponse.getData().getItems().get(0);
                    productList.add(new Product(productList.size() + 1, 
                        item.getTitle(), upcCode, new com.google.gson.Gson().toJson(item)));
                    adapter.notifyItemInserted(productList.size() - 1);
                    updateView();
                } catch (Exception e) {
                    Log.e("ScanFragment", "Error using sample data after network failure", e);
                    // Fall back to the old method if anything goes wrong
                    addScannedProduct(upcCode);
                }
            }
        });
    }
    
    // This method has been replaced by direct JSON conversion and intent creation in the API response callback
    // The old method has been removed as it's no longer needed

    // Create a new product using random name & random details from the loaded JSON
    private void addScannedProduct(String scannedCode) {
        try {
            // Pick a random product name
            JSONArray randomProdArray = new JSONArray(randomProducts);
            int randomIndex = new Random().nextInt(randomProdArray.length());
            JSONObject randomProd = randomProdArray.getJSONObject(randomIndex);
            String randomName = randomProd.getString("name");

            // Pick random extra details from the loaded datas.json array
            JSONArray detailsArray = new JSONArray(dummyJson);
            int detailsIndex = new Random().nextInt(detailsArray.length());
            JSONObject detailsObj = detailsArray.getJSONObject(detailsIndex).optJSONObject("product");
            String detailsString = (detailsObj != null) ? detailsObj.toString() : "";

            // Generate new id
            int newId = productList.size() + 1;
            String productName = randomName + " - " + scannedCode;

            // Create a new JSON object for the scanned product
            JSONObject newProduct = new JSONObject();
            newProduct.put("id", newId);
            newProduct.put("name", productName);
            newProduct.put("code", scannedCode);
            newProduct.put("product", detailsObj);

            // Append new product to the original JSON
            originalProductsJson.put(newProduct);
            dummyJson = originalProductsJson.toString();

            // Add to list
            productList.add(new Product(newId, productName, scannedCode, detailsString));
            adapter.notifyItemInserted(productList.size() - 1);
            updateView();

            Toast.makeText(getContext(), "New product added!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update the UI
    private void updateView() {
        if (productList.isEmpty()) {
            tvNoProducts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoProducts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Data model class for a product
    public static class Product {
        public int id;
        public String name;
        public String code;
        public String details; // store product JSON or extra fields

        public Product(int id, String name, String code, String details) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.details = details;
        }
    }

    // RecyclerView Adapter
// inside ScanFragment.java

    // RecyclerView Adapter for products
    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.tvProductName.setText(product.name);
            
            // Try to extract and display price from product details
            try {
                if (product.details != null && !product.details.isEmpty()) {
                    JSONObject detailsObj = new JSONObject(product.details);
                    if (detailsObj.has("price")) {
                        JSONObject priceObj = detailsObj.getJSONObject("price");
                        if (priceObj.has("original")) {
                            double price = priceObj.getDouble("original");
                            String priceDisplay = String.format("$%.2f", price);
                            holder.tvProductPrice.setText(priceDisplay);
                            holder.tvProductPrice.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    holder.tvProductPrice.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                holder.tvProductPrice.setVisibility(View.GONE);
                Log.e("ScanFragment", "Error parsing price information", e);
            }

            // Open ProductDetailsActivity on product name click
            holder.tvProductName.setOnClickListener(v -> {
                JSONObject productObj = new JSONObject();
                try {
                    productObj.put("id", product.id);
                    productObj.put("name", product.name);
                    productObj.put("code", product.code);
                    if (product.details != null && !product.details.isEmpty()) {
                        JSONObject detailsObj = new JSONObject(product.details);
                        productObj.put("product", detailsObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("productJson", productObj.toString());
                startActivity(intent);
            });

            // Wish button click: add product to wishlist (offerJson) and call API to add to cart
            holder.btnWish.setOnClickListener(v -> {
                JSONObject wishProductObj = new JSONObject();
                try {
                    wishProductObj.put("id", product.id);
                    wishProductObj.put("name", product.name);
                    wishProductObj.put("code", product.code);
                    if (product.details != null && !product.details.isEmpty()) {
                        JSONObject detailsObj = new JSONObject(product.details);
                        wishProductObj.put("product", detailsObj);
                    }
                    
                    // First add to local storage for immediate UI feedback
                    com.example.mobileshop.models.OfferStore.offerJson.put(wishProductObj);
                    
                    if (getView() != null) {
                        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(
                            getView(),
                            "Adding to cart...", 
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        );
                        snackbar.setAction("VIEW CART", view -> {
                            // Navigate to cart tab (index 2)
                            androidx.fragment.app.FragmentActivity activity = getActivity();
                            if (activity instanceof com.example.mobileshop.views.DashboardActivity) {
                                com.google.android.material.bottomnavigation.BottomNavigationView navView = 
                                    activity.findViewById(R.id.bottom_navigation);
                                navView.setSelectedItemId(R.id.nav_cart);
                            }
                        });
                        snackbar.show();
                    } else {
                        Toast.makeText(getContext(), "Adding to cart...", Toast.LENGTH_SHORT).show();
                    }
                    
                    // Get user ID from session manager
                    SessionManager sessionManager = new SessionManager(getContext());
                    String userId = sessionManager.getUserId();
                    
                    // If the user is not logged in, use a default value
                    if (userId == null || userId.isEmpty()) {
                        userId = "6829021c4d26cf04aba961cc"; // Fallback to default user ID
                        Log.d("ScanFragment", "User ID not found in session, using default");
                    }
                    
                    // Make API call to add to cart
                    CartApiService apiService = RetrofitClient.getClient().create(CartApiService.class);
                    final String finalUserId = userId;
                    
                    // Extract product details for the request
                    String productId = product.code;
                    String productName = product.name;
                    String productDescription = "";
                    String productBrand = "";
                    double productPrice = 0.0;
                    String productLink = "";
                    String imageLink = "";
                    
                    try {
                        if (product.details != null && !product.details.isEmpty()) {
                            JSONObject detailsObj = new JSONObject(product.details);
                            
                            // Extract description
                            if (detailsObj.has("description")) {
                                productDescription = detailsObj.getString("description");
                            }
                            
                            // Extract brand
                            if (detailsObj.has("brand")) {
                                productBrand = detailsObj.getString("brand");
                            }
                            
                            // Extract price
                            if (detailsObj.has("price")) {
                                JSONObject priceObj = detailsObj.getJSONObject("price");
                                if (priceObj.has("original")) {
                                    String priceStr = priceObj.optString("original", "");
                                    try {
                                        if (priceStr != null && !priceStr.isEmpty()) {
                                            productPrice = Double.parseDouble(priceStr);
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("ScanFragment", "Error parsing price: " + priceStr, e);
                                        productPrice = 0.0;
                                    }
                                }
                            }
                            
                            // Extract product link
                            if (detailsObj.has("product_link")) {
                                productLink = detailsObj.getString("product_link");
                            }
                            
                            // Extract image link
                            if (detailsObj.has("image")) {
                                imageLink = detailsObj.getString("image");
                            } else if (detailsObj.has("images") && detailsObj.getJSONArray("images").length() > 0) {
                                imageLink = detailsObj.getJSONArray("images").getString(0);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("ScanFragment", "Error parsing product details", e);
                    }
                    
                    // Create request object
                    AddToCartRequest request = new AddToCartRequest(
                        productId,
                        productName,
                        productDescription,
                        productBrand,
                        productPrice,
                        productLink,
                        imageLink
                    );
                    
                    apiService.addToCart(finalUserId, request).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(retrofit2.Call<ApiResponse<Void>> call, retrofit2.Response<ApiResponse<Void>> response) {
                            Log.e("ScanFragment", "response: " + response.toString() + 
                                    ", response body: " + (response.body() != null ? response.body().toString() : "null"));
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                // API call successful - no need for another notification as we already showed a snackbar
                                Log.d("ScanFragment", "Successfully added product to cart");
                            } else {
                                // API call failed but we already added to local storage
                                String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to add to cart on server, but added locally";
                                // Show a short toast with the error message
                                Log.d("ScanFragment", "Failed to add product to cart: " + errorMsg);
                                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ApiResponse<Void>> call, Throwable t) {
                            // Network failure
                            Toast.makeText(getContext(), "Network error, but product was added to local cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Delete button click: remove product from list
            holder.btnDelete.setOnClickListener(v -> removeProduct(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView tvProductName;
            TextView tvProductPrice;
            android.widget.ImageButton btnDelete;
            android.widget.ImageButton btnWish;

            public ProductViewHolder(View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnWish = itemView.findViewById(R.id.btnWish);
            }
        }
    }


    // Remove product at the given position
    private void removeProduct(int position) {
        if (position >= 0 && position < productList.size()) {
            Product product = productList.get(position);
            
            // Get user ID from session manager
            SessionManager sessionManager = new SessionManager(getContext());
            String userId = sessionManager.getUserId();
            
            // If the user is not logged in, use a default value
            if (userId == null || userId.isEmpty()) {
                userId = "6828f6f94d26cf04aba961ca"; // Fallback to default user ID
            }
            
            // Make API call to delete from history
            CartApiService apiService = RetrofitClient.getClient().create(CartApiService.class);
            
            final String finalUserId = userId;
            final int finalPosition = position;
            
            // Show a loading toast
            Toast.makeText(getContext(), "Deleting product...", Toast.LENGTH_SHORT).show();
            
            apiService.deleteFromHistory(finalUserId, product.code).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse<Void>> call, retrofit2.Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // API call successful - remove product from local list
                        productList.remove(finalPosition);
                        adapter.notifyItemRemoved(finalPosition);
                        updateView();
                        Toast.makeText(getContext(), "Product deleted from history", Toast.LENGTH_SHORT).show();
                    } else {
                        // API call failed - show error
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to delete product";
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        
                        // Remove locally anyway for better UX
                        productList.remove(finalPosition);
                        adapter.notifyItemRemoved(finalPosition);
                        updateView();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ApiResponse<Void>> call, Throwable t) {
                    // Network failure - show error but remove locally for better UX
                    Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    productList.remove(finalPosition);
                    adapter.notifyItemRemoved(finalPosition);
                    updateView();
                }
            });
        }
    }
    
    // Check if the device has network connectivity
    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) 
            getContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
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
