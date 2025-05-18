package com.example.mobileshop.api;

import com.example.mobileshop.models.ProductLookupRequest;
import com.example.mobileshop.models.ProductLookupResponse;
import com.example.mobileshop.models.ProductHistoryResponse;
import com.example.mobileshop.models.ApiProductResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProductApiService {
    @POST("api/product/{userId}/lookup")
    Call<ApiProductResponse> lookupProduct(
        @Path("userId") String userId,
        @Body ProductLookupRequest request
    );
    
    @GET("api/product/{userId}/history")
    Call<ProductHistoryResponse> getProductHistory(
        @Path("userId") String userId
    );
}