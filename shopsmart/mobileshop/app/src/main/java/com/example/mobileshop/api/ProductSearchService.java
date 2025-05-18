package com.example.mobileshop.api;

import com.example.mobileshop.models.ApiResponse;
import com.example.mobileshop.models.ProductSearchRequest;
import com.example.mobileshop.models.ProductSearchResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProductSearchService {
    /**
     * Search for products by name or UPC code 
     */
    @POST("api/search/{userId}/product-search")
    Call<ApiResponse<ProductSearchResponse>> searchProducts(
        @Path("userId") String userId,
        @Body ProductSearchRequest request
    );
}
