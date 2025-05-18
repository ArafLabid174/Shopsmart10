package com.shopsmart.shopsmart_server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.dto.DeepSearchRequest;
import com.shopsmart.shopsmart_server.model.DeepSearchResult;
import com.shopsmart.shopsmart_server.service.ProductSearchService;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class ProductSearchController {

    private final ProductSearchService productSearchService;
    
    @Autowired
    public ProductSearchController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }
    
    @PostMapping("/{userId}/product-search")
    public ResponseEntity<ApiResponse<DeepSearchResult>> productSearch(
            @PathVariable String userId,
            @RequestBody DeepSearchRequest request) {
        
        ApiResponse<DeepSearchResult> response = productSearchService.searchProductsAcrossDomains(userId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/{userId}/history")
    public ResponseEntity<ApiResponse<List<DeepSearchResult>>> getSearchHistory(@PathVariable String userId) {
        ApiResponse<List<DeepSearchResult>> response = productSearchService.getSearchHistoryByUserId(userId);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}