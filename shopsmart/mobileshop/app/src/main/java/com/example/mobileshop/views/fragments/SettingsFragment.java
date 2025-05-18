package com.example.mobileshop.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.mobileshop.R;
import com.example.mobileshop.utils.SessionManager;
import com.example.mobileshop.views.LoginActivity;

/**
 * Fragment for app settings page
 * Handles user preferences and logout functionality
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required for fragments
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the settings layout
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        
        // Initialize logout option
        setupLogoutOption(view);
        
        return view;
    }
    
    /**
     * Clears user session and redirects to login screen
     */
    private void setupLogoutOption(View view) {
        LinearLayout logoutOption = view.findViewById(R.id.logoutOption);
        logoutOption.setOnClickListener(v -> {
            // Get session manager and clear user data
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.logoutUser();
            
            // Redirect to login screen
            Intent loginIntent = new Intent(requireActivity(), LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            requireActivity().finish();
        });
    }
}
