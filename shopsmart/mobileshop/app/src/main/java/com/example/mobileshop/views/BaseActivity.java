package com.example.mobileshop.views;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileshop.utils.LoadingManager;

/**
 * Base activity class that provides common functionality for all activities
 * including the loading indicator management.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Shows the loading indicator with a custom message
     *
     * @param message The message to display in the loading indicator
     */
    protected void showLoading(String message) {
        LoadingManager.showLoading(this, message);
    }

    /**
     * Shows the loading indicator with the default "Loading..." message
     */
    protected void showLoading() {
        LoadingManager.showLoading(this);
    }

    /**
     * Hides the loading indicator
     */
    protected void hideLoading() {
        LoadingManager.hideLoading(this);
    }
    
    @Override
    protected void onDestroy() {
        // Always hide the loading indicator when the activity is destroyed
        hideLoading();
        super.onDestroy();
    }
}
