package com.example.mobileshop.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import com.example.mobileshop.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductCode, tvProductPrice;
    private TextView tvFeaturesTitle, tvFeaturesList, tvTipsTitle, tvTipsContent;
    private TextView tvReviewsTitle, tvReviewsSummary;
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
        btnCompare = findViewById(R.id.btnCompare);

        // Retrieve the product JSON from intent
        String productJsonStr = getIntent().getStringExtra("productJson");
        if (productJsonStr != null) {
            try {
                JSONObject productObj = new JSONObject(productJsonStr);

                // For example: name, code, image, price, sections, etc.
                String name = productObj.optString("name", "Unknown Product");
                String code = productObj.optString("code", "N/A");

                // If there's a nested 'product' object, parse its details
                JSONObject detailsObj = productObj.optJSONObject("product");
                if (detailsObj != null) {
                    // e.g. image URL, price info, features
                    String imageUrl = detailsObj.optString("image", "");
                    JSONObject priceObj = detailsObj.optJSONObject("price");
                    if (priceObj != null) {
                        String originalPrice = priceObj.optString("original", "");
                        tvProductPrice.setText("Price: " + originalPrice);
                    }
                    JSONObject sectionsObj = detailsObj.optJSONObject("sections");
                    if (sectionsObj != null) {
                        // features array
                        StringBuilder featuresBuilder = new StringBuilder();
                        if (sectionsObj.has("features")) {
                            for (int i = 0; i < sectionsObj.getJSONArray("features").length(); i++) {
                                featuresBuilder.append("• ")
                                        .append(sectionsObj.getJSONArray("features").getString(i))
                                        .append("\n");
                            }
                        }
                        tvFeaturesList.setText(featuresBuilder.toString().trim());

                        // tips_and_advice
                        String tips = sectionsObj.optString("tips_and_advice", "No tips provided");
                        tvTipsContent.setText(tips);
                    }

                    // reviews
                    JSONObject reviewsObj = detailsObj.optJSONObject("reviews");
                    if (reviewsObj != null) {
                        double rating = reviewsObj.optDouble("rating", 0);
                        int totalReviews = reviewsObj.optInt("total_reviews", 0);
                        tvReviewsSummary.setText(
                                rating + " out of 5 (" + totalReviews + " reviews)"
                        );
                    }
                }

                // Set text fields
                tvProductName.setText(name);
                tvProductCode.setText("Code: " + code);

                // TODO: If you want to load the image from URL, use an image loading library
                // e.g. Picasso or Glide
                // Glide.with(this).load(imageUrl).into(ivProductImage);
                Glide.with(this).load("https://m.media-amazon.com/images/I/71TXUkQhg0L._SL1500_.jpg").into(ivProductImage);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Handle "Compare" button
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open comparison screen
                Intent intent = new Intent(ProductDetailsActivity.this, ProductComparisonActivity.class);
                // Optionally pass more data about this product if needed
                startActivity(intent);
            }
        });
    }
}
