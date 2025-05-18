package com.example.mobileshop.api;

import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.AddToCartRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartApiService {      // Add item to cart
    @POST("api/cart/{userId}/add")
    Call<ApiResponse<Void>> addToCart(
        @Path("userId") String userId,
        @Body AddToCartRequest request
    );
    
    // Delete item from history
    @DELETE("api/product/{userId}/history/{upcCode}")
    Call<ApiResponse<Void>> deleteFromHistory(
        @Path("userId") String userId,
        @Path("upcCode") String upcCode
    );
}
