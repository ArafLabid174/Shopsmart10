package com.example.mobileshop.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.mobileshop.views.ProductDetailsActivity;
import com.example.mobileshop.views.ScanOptionsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;



public class ScanFragment extends Fragment {

    private RecyclerView recyclerView;
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

        // Load products
        loadDummyData();

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

    // Populate productList from the originalProductsJson array
    private void loadDummyData() {
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
                addScannedProduct(scannedCode);
            }
        }
    }

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

            // Wish button click: add product to wishlist (offerJson)
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
                    // Add product to the global offerJson array
                    com.example.mobileshop.models.OfferStore.offerJson.put(wishProductObj);
                    Toast.makeText(getContext(), "Product added to wishlist", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
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
            android.widget.ImageButton btnDelete;
            android.widget.ImageButton btnWish;

            public ProductViewHolder(View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnWish = itemView.findViewById(R.id.btnWish);
            }
        }
    }


    // Remove product at the given position
    private void removeProduct(int position) {
        if (position >= 0 && position < productList.size()) {
            productList.remove(position);
            adapter.notifyItemRemoved(position);
            updateView();
        }
    }
}
