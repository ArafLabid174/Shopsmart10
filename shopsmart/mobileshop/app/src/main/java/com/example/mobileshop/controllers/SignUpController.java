package com.example.mobileshop.controllers;

import android.content.Context;

import android.widget.Toast;

import com.example.mobileshop.api.AuthApiService;
import com.example.mobileshop.api.RetrofitClient;
import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.SignUpRequest;
import com.example.mobileshop.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpController {

    private final AuthApiService apiService;

    public SignUpController() {
        apiService = RetrofitClient.getClient().create(AuthApiService.class);
    }

    public interface SignUpCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public void signUp(String fullName, String email, String password, String confirmPassword, Context context, SignUpCallback callback) {
        // Validate input fields
        if(fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.onError("All fields are required");
            }
            return;
        } 
        
        if(!password.equals(confirmPassword)) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.onError("Passwords do not match");
            }
            return;
        }

        // Create signup request
        SignUpRequest request = new SignUpRequest(fullName, email, password, confirmPassword);

        // Make API call
        Call<ApiResponse<User>> call = apiService.signup(request);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        User user = apiResponse.getData();
                        
                        // Save user data to SharedPreferences
                        if (user != null) {
                            com.example.mobileshop.utils.SessionManager sessionManager = new com.example.mobileshop.utils.SessionManager(context);
                            sessionManager.createLoginSession(user.getId(), user.getFullName(), user.getEmail());
                        }
                        
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (callback != null) {
                            callback.onSuccess(apiResponse.getData());
                        }
                    } else {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (callback != null) {
                            callback.onError(apiResponse.getMessage());
                        }
                    }
                } else {
                    Toast.makeText(context, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    if (callback != null) {
                        callback.onError("Server error: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();

                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                if (callback != null) {
                    callback.onError(errorMsg);
                }
            }
        });
    }

    // Keep old method for backward compatibility
    public void signUp(String fullName, String email, String password, String confirmPassword, Context context) {
        signUp(fullName, email, password, confirmPassword, context, null);
    }
}
