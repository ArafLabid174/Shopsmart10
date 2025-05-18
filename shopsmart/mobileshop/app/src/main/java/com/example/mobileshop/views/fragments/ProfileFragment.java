package com.example.mobileshop.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobileshop.R;
import com.example.mobileshop.api.AuthApiService;
import com.example.mobileshop.api.RetrofitClient;
import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.UpdateUserRequest;
import com.example.mobileshop.models.User;
import com.example.mobileshop.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    
    private SessionManager sessionManager;
    private AuthApiService authApiService;
    
    // UI components
    private TextInputEditText etFullName;
    private TextView tvUserEmail;
    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private Button btnSubmit;
    private ImageView ivEditFullName;
    private ImageView ivEditCurrentPassword;
    private ImageView ivEditNewPassword;
    private TextInputLayout tilFullName;
    private TextInputLayout tilCurrentPassword;
    private TextInputLayout tilNewPassword;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for ProfileFragment
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize services
        sessionManager = new SessionManager(requireContext());
        authApiService = RetrofitClient.getClient().create(AuthApiService.class);
        
        // Initialize UI components
        initializeViews(view);
        
        // Load user data from session
        loadUserData();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void initializeViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        
        ivEditFullName = view.findViewById(R.id.ivEditFullName);
        ivEditCurrentPassword = view.findViewById(R.id.ivEditCurrentPassword);
        ivEditNewPassword = view.findViewById(R.id.ivEditNewPassword);
        
        tilFullName = view.findViewById(R.id.tilFullName);
        tilCurrentPassword = view.findViewById(R.id.tilCurrentPassword);
        tilNewPassword = view.findViewById(R.id.tilNewPassword);
    }
    
    private void loadUserData() {
        // Load user data from SessionManager
        String fullName = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();
        
        if (fullName != null) {
            etFullName.setText(fullName);
        }
        
        if (email != null) {
            tvUserEmail.setText(email);
        }
    }
    
    private void setupClickListeners() {
        // Toggle edit mode for full name
        ivEditFullName.setOnClickListener(v -> {
            boolean isEnabled = !etFullName.isEnabled();
            etFullName.setEnabled(isEnabled);
            if (isEnabled) {
                etFullName.requestFocus();
                ivEditFullName.setImageResource(R.drawable.ic_done);
            } else {
                ivEditFullName.setImageResource(R.drawable.ic_edit);
            }
        });
        
        // Toggle edit mode for current password
        ivEditCurrentPassword.setOnClickListener(v -> {
            boolean isEnabled = !etCurrentPassword.isEnabled();
            etCurrentPassword.setEnabled(isEnabled);
            if (isEnabled) {
                etCurrentPassword.requestFocus();
                ivEditCurrentPassword.setImageResource(R.drawable.ic_done);
            } else {
                ivEditCurrentPassword.setImageResource(R.drawable.ic_edit);
            }
        });
        
        // Toggle edit mode for new password
        ivEditNewPassword.setOnClickListener(v -> {
            boolean isEnabled = !etNewPassword.isEnabled();
            etNewPassword.setEnabled(isEnabled);
            if (isEnabled) {
                etNewPassword.requestFocus();
                ivEditNewPassword.setImageResource(R.drawable.ic_done);
            } else {
                ivEditNewPassword.setImageResource(R.drawable.ic_edit);
            }
        });
        
        // Submit button click listener
        btnSubmit.setOnClickListener(v -> {
            submitProfileUpdate();
        });
    }
    
    private void submitProfileUpdate() {
        // Get current values from fields
        String fullName = etFullName.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String userId = sessionManager.getUserId();
        
        // Validation
        if (TextUtils.isEmpty(currentPassword)) {
            tilCurrentPassword.setError("Current password is required");
            return;
        }
        
        // Prepare the update request
        UpdateUserRequest updateRequest = new UpdateUserRequest(
            etFullName.isEnabled() ? fullName : null,
            currentPassword,
            etNewPassword.isEnabled() && !TextUtils.isEmpty(newPassword) ? newPassword : null
        );
        
        // Send the update request
        updateUserProfile(userId, updateRequest);
    }
    
    private void updateUserProfile(String userId, UpdateUserRequest updateRequest) {
        // Show loading state
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Updating...");
        
        // Make API call
        Call<ApiResponse<User>> call = authApiService.updateUser(userId, updateRequest);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                // Reset button state
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit");
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Update successful
                        User updatedUser = apiResponse.getData();
                        
                        // Update session data
                        sessionManager.createLoginSession(
                            updatedUser.getId(),
                            updatedUser.getFullName(),
                            updatedUser.getEmail()
                        );
                        
                        // Reset UI state
                        etFullName.setEnabled(false);
                        etCurrentPassword.setEnabled(false);
                        etCurrentPassword.setText("");
                        etNewPassword.setEnabled(false);
                        etNewPassword.setText("");
                        ivEditFullName.setImageResource(R.drawable.ic_edit);
                        ivEditCurrentPassword.setImageResource(R.drawable.ic_edit);
                        ivEditNewPassword.setImageResource(R.drawable.ic_edit);
                        
                        // Clear any error messages
                        tilFullName.setError(null);
                        tilCurrentPassword.setError(null);
                        tilNewPassword.setError(null);
                        
                        // Show success message
                        Toast.makeText(requireContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Reload user data
                        loadUserData();
                    } else {
                        // API returned success=false
                        Toast.makeText(requireContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle error response
                    String errorMessage = "Failed to update profile. Please try again.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Reset button state
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit");
                
                // Show error message
                Log.e(TAG, "API call failed", t);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
