package com.example.mobileshop.views;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductComparisonActivity extends AppCompatActivity {

    private TableLayout tableComparison;
    // Some dummy JSON data for demonstration
    private String dummyComparisonData = "[\n" +
            "  {\"shop\":\"Amazon\",\"price\":\"$13.20\",\"rating\":\"4.3\"},\n" +
            "  {\"shop\":\"Alibaba\",\"price\":\"$12.80\",\"rating\":\"4.0\"},\n" +
            "  {\"shop\":\"eBay\",\"price\":\"$14.00\",\"rating\":\"4.1\"}\n" +
            "]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_comparison);

        tableComparison = findViewById(R.id.tableComparison);

        // Dynamically add rows to the table
        try {
            JSONArray compArray = new JSONArray(dummyComparisonData);
            for (int i = 0; i < compArray.length(); i++) {
                JSONObject rowObj = compArray.getJSONObject(i);
                String shop = rowObj.optString("shop", "Unknown");
                String price = rowObj.optString("price", "$0.00");
                String rating = rowObj.optString("rating", "N/A");

                TableRow row = new TableRow(this);

                TextView shopText = new TextView(this);
                shopText.setText(shop);
                shopText.setPadding(8, 8, 8, 8);

                TextView priceText = new TextView(this);
                priceText.setText(price);
                priceText.setPadding(8, 8, 8, 8);

                TextView ratingText = new TextView(this);
                ratingText.setText(rating);
                ratingText.setPadding(8, 8, 8, 8);

                row.addView(shopText);
                row.addView(priceText);
                row.addView(ratingText);

                tableComparison.addView(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
