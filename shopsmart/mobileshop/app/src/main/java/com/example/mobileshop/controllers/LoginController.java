package com.example.mobileshop.controllers;

import android.content.Context;
import android.content.Intent;

import android.widget.Toast;

import com.example.mobileshop.api.AuthApiService;
import com.example.mobileshop.api.RetrofitClient;
import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.SignInRequest;
import com.example.mobileshop.models.User;
import com.example.mobileshop.utils.SessionManager;
import com.example.mobileshop.views.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController {

    private final AuthApiService apiService;
    
    public LoginController() {
        apiService = RetrofitClient.getClient().create(AuthApiService.class);
    }
    
    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String message);
    }
    
    public void login(String email, String password, Context context, LoginCallback callback) {
        // Check if email or password is empty
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.onError("Please enter email and password");
            }
            return;
        }
        
        // Create signin request
        SignInRequest request = new SignInRequest(email, password);
        
        // Make API call
        Call<ApiResponse<User>> call = apiService.signin(request);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        User user = apiResponse.getData();
                        
                        // Save user data to SharedPreferences
                        if (user != null) {
                            SessionManager sessionManager = new SessionManager(context);
                            sessionManager.createLoginSession(user.getId(), user.getFullName(), user.getEmail());
                        }
                        
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Redirect to DashboardActivity
                        Intent intent = new Intent(context, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        
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
                    String errorMsg = "Server error: " + response.message();
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    if (callback != null) {
                        callback.onError(errorMsg);
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
    public void login(String email, String password, Context context) {
        login(email, password, context, null);
    }
}
