package com.example.mobileshop.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileshop.R;
import com.example.mobileshop.models.OfferStore;

import org.json.JSONException;
import org.json.JSONObject;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoOffers;
    private OfferAdapter adapter;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        recyclerView = view.findViewById(R.id.rvOfferProducts);
        tvNoOffers = view.findViewById(R.id.tvNoOffers);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OfferAdapter();
        recyclerView.setAdapter(adapter);
        updateView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list each time the fragment is resumed
        adapter.notifyDataSetChanged();
        updateView();
    }

    private void updateView() {
        if (OfferStore.offerJson.length() == 0) {
            tvNoOffers.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoOffers.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Adapter to show wished products
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
                holder.tvProductName.setText(name);

                // Optional: open product details on product name click
                holder.tvProductName.setOnClickListener(v ->
                        Toast.makeText(getContext(), "Clicked on " + name, Toast.LENGTH_SHORT).show()
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return OfferStore.offerJson.length();
        }

        class OfferViewHolder extends RecyclerView.ViewHolder {
            TextView tvProductName;

            public OfferViewHolder(View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
            }
        }
    }
}
