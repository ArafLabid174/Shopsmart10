package com.example.mobileshop.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobileshop.R;

public class LoadingManager {
    
    private static View getLoadingLayout(Activity activity) {
        ViewGroup rootView = activity.findViewById(android.R.id.content);
        View loadingView = activity.getLayoutInflater().inflate(R.layout.layout_loading, null);
        
        // Check if loading layout is already added to avoid duplicates
        int loadingLayoutId = R.id.loadingLayout;
        View existingLoadingView = rootView.findViewById(loadingLayoutId);
        if (existingLoadingView != null) {
            return existingLoadingView;
        }
        
        // Add loading layout to the root view as an overlay
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        rootView.addView(loadingView, params);
        
        return loadingView;
    }
    
    public static void showLoading(Activity activity) {
        showLoading(activity, "Loading...");
    }
    
    public static void showLoading(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        
        View loadingLayout = getLoadingLayout(activity);
        TextView tvMessage = loadingLayout.findViewById(R.id.tvLoadingMessage);
        tvMessage.setText(message);
        
        loadingLayout.setVisibility(View.VISIBLE);
    }
    
    public static void hideLoading(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        
        View loadingLayout = activity.findViewById(R.id.loadingLayout);
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
    }
}
