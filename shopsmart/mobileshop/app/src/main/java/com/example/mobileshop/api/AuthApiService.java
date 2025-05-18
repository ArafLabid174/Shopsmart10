package com.example.mobileshop.api;

import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.SignInRequest;
import com.example.mobileshop.models.SignUpRequest;
import com.example.mobileshop.models.User;
import com.example.mobileshop.models.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AuthApiService {
    @POST("api/auth/signup")
    Call<ApiResponse<User>> signup(@Body SignUpRequest request);
    
    @POST("api/auth/signin")
    Call<ApiResponse<User>> signin(@Body SignInRequest request);
    
    @PUT("api/auth/users/{userId}")
    Call<ApiResponse<User>> updateUser(@Path("userId") String userId, @Body UpdateUserRequest request);
}
