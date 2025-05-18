package com.example.mobileshop.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.mobileshop.R;

public class OffersFragment extends Fragment {

    public OffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for OffersFragment
        return inflater.inflate(R.layout.offer_fragment, container, false);
    }
}
