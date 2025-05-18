package com.example.mobileshop.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileshop.R;
import com.example.mobileshop.api.CartApiService;
import com.example.mobileshop.api.RetrofitClient;
import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.OfferStore;
import com.example.mobileshop.utils.SessionManager;
import com.example.mobileshop.views.ProductDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoOffers;
    private TextView tvItemCount;
    private Button btnCheckout;
    private LinearLayout cartSummaryLayout;
    private OfferAdapter adapter;
    private static final String TAG = "CartFragment";

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        recyclerView = view.findViewById(R.id.rvOfferProducts);
        tvNoOffers = view.findViewById(R.id.tvNoOffers);
        tvItemCount = view.findViewById(R.id.tvItemCount);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        cartSummaryLayout = view.findViewById(R.id.cartSummaryLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OfferAdapter();
        recyclerView.setAdapter(adapter);
        
        // Set up checkout button
        btnCheckout.setOnClickListener(v -> handleCheckout());
        
        updateView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list each time the fragment is resumed
        refreshCart();
    }
    
    /**
     * Refreshes the cart contents
     */
    private void refreshCart() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            updateView();
        }
    }
    
    /**
     * Handles the checkout process
     */
    private void handleCheckout() {
        int itemCount = OfferStore.offerJson.length();
        if (itemCount > 0) {
            Toast.makeText(getContext(), "Processing checkout for " + itemCount + " items...", Toast.LENGTH_LONG).show();
            
            // For this demo, clear the cart after a fake checkout process
            new android.os.Handler().postDelayed(() -> {
                Toast.makeText(getContext(), "Thank you for your purchase!", Toast.LENGTH_SHORT).show();
                // Clear the cart
                OfferStore.offerJson = new JSONArray();
                adapter.notifyDataSetChanged();
                updateView();
            }, 2000);
        } else {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() {
        int itemCount = OfferStore.offerJson.length();
        
        // Update item count
        tvItemCount.setText(String.valueOf(itemCount));
        
        if (itemCount == 0) {
            tvNoOffers.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            cartSummaryLayout.setVisibility(View.GONE);
        } else {
            tvNoOffers.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            cartSummaryLayout.setVisibility(View.VISIBLE);
            
            // Calculate cart total
            calculateCartTotal();
        }
    }
    
    /**
     * Calculates the total price of all items in the cart and updates the UI
     */
    private void calculateCartTotal() {
        double total = 0.0;
        
        for (int i = 0; i < OfferStore.offerJson.length(); i++) {
            try {
                JSONObject productObj = OfferStore.offerJson.getJSONObject(i);
                if (productObj.has("product")) {
                    JSONObject details = productObj.getJSONObject("product");
                    if (details.has("price")) {
                        JSONObject priceObj = details.getJSONObject("price");
                        if (priceObj.has("original")) {
                            total += priceObj.getDouble("original");
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error calculating cart total", e);
            }
        }
        
        // If we have a TextView for total, update it
        View view = getView();
        if (view != null) {
            TextView tvTotal = view.findViewById(R.id.tvTotalPrice);
            if (tvTotal != null) {
                tvTotal.setText(String.format("$%.2f", total));
            }
        }
    }
    
    /**
     * Removes an item from the cart and calls the API to delete it from history
     * @param position The position of the item to remove
     */
    private void removeCartItem(int position) {
        try {
            if (position >= 0 && position < OfferStore.offerJson.length()) {
                // Get the product object and UPC code
                JSONObject productObj = OfferStore.offerJson.getJSONObject(position);
                String upcCode = productObj.optString("code", "");
                
                if (upcCode.isEmpty()) {
                    Toast.makeText(getContext(), "Error: Product code not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Get user ID from session manager
                SessionManager sessionManager = new SessionManager(getContext());
                String userId = sessionManager.getUserId();
                
                // If the user is not logged in, use a default value
                if (userId == null || userId.isEmpty()) {
                    userId = "6828f6f94d26cf04aba961ca"; // Fallback to default user ID
                }
                
                // First remove from local storage for immediate UI feedback
                OfferStore.offerJson = removeJsonObjectFromArray(OfferStore.offerJson, position);
                adapter.notifyItemRemoved(position);
                updateView();
                
                // Show loading toast
                Toast.makeText(getContext(), "Removing item from cart...", Toast.LENGTH_SHORT).show();
                
                // Make API call to delete from history
                CartApiService apiService = RetrofitClient.getClient().create(CartApiService.class);
                final String finalUserId = userId;
                
                apiService.deleteFromHistory(finalUserId, upcCode).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(retrofit2.Call<ApiResponse<Void>> call, retrofit2.Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            // API call successful
                            Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                        } else {
                            // API call failed - show error
                            String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to remove item on server";
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ApiResponse<Void>> call, Throwable t) {
                        // Network failure
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("CartFragment", "API call failed", t);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("CartFragment", "Error removing cart item", e);
        }
    }
    
    /**
     * Helper method to remove an item from a JSONArray
     * @param array The source array
     * @param position Position to remove
     * @return New JSONArray with the item removed
     */
    private JSONArray removeJsonObjectFromArray(JSONArray array, int position) {
        JSONArray newArray = new JSONArray();
        try {
            for (int i = 0; i < array.length(); i++) {
                if (i != position) {
                    newArray.put(array.get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newArray;
    }

    // Adapter to show cart products
    private class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

        @Override
        public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new OfferViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OfferViewHolder holder, int position) {
            try {
                JSONObject productObj = OfferStore.offerJson.getJSONObject(position);
                String name = productObj.optString("name", "Unnamed Product");
                String code = productObj.optString("code", "");
                holder.tvProductName.setText(name);

                // Try to extract and display price information if available
                try {
                    if (productObj.has("product")) {
                        JSONObject details = productObj.getJSONObject("product");
                        if (details.has("price")) {
                            JSONObject priceObj = details.getJSONObject("price");
                            String priceDisplay;
                            
                            if (priceObj.has("original")) {
                                double price = priceObj.getDouble("original");
                                priceDisplay = String.format("$%.2f", price);
                                
                                if (priceObj.has("lowest") && priceObj.has("highest")) {
                                    double lowest = priceObj.getDouble("lowest");
                                    double highest = priceObj.getDouble("highest");
                                    priceDisplay = String.format("$%.2f (Range: $%.2f - $%.2f)", 
                                                                price, lowest, highest);
                                }
                                
                                holder.tvProductPrice.setText(priceDisplay);
                                holder.tvProductPrice.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (JSONException e) {
                    // If there's any error parsing price, just hide the price view
                    holder.tvProductPrice.setVisibility(View.GONE);
                    Log.e("CartFragment", "Error parsing price information", e);
                }

                // Product name click: Open product details
                holder.tvProductName.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                        intent.putExtra("productJson", productObj.toString());
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error opening product details", Toast.LENGTH_SHORT).show();
                        Log.e("CartFragment", "Error opening product details", e);
                    }
                });
                
                // Hide wish button in cart view (it's already in the cart)
                holder.btnWish.setVisibility(View.GONE);
                
                // Show delete button and enable click
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> removeCartItem(position));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("CartFragment", "Error binding cart item", e);
            }
        }

        @Override
        public int getItemCount() {
            return OfferStore.offerJson.length();
        }

        class OfferViewHolder extends RecyclerView.ViewHolder {
            TextView tvProductName;
            TextView tvProductPrice;
            ImageButton btnWish;
            ImageButton btnDelete;

            public OfferViewHolder(View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
                btnWish = itemView.findViewById(R.id.btnWish);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
