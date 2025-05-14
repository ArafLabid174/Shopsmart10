package com.shopsmart.shopsmart_server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.dto.UpcLookupRequest;
import com.shopsmart.shopsmart_server.model.LookupHistory;
import com.shopsmart.shopsmart_server.service.ProductLookupService;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductLookupController {

    private final ProductLookupService productLookupService;
    
    @Autowired
    public ProductLookupController(ProductLookupService productLookupService) {
        this.productLookupService = productLookupService;
    }
    
    @PostMapping("/{userId}/lookup")
    public ResponseEntity<ApiResponse<JsonNode>> lookupProductByUpc(
            @PathVariable String userId,
            @RequestBody UpcLookupRequest request) {
        
        if (request.getUpcCode() == null || request.getUpcCode().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("UPC code is required"));
        }
        
        ApiResponse<JsonNode> response = productLookupService.lookupProductByUpc(userId, request.getUpcCode());
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/{userId}/history")
    public ResponseEntity<ApiResponse<List<LookupHistory>>> getLookupHistory(@PathVariable String userId) {
        ApiResponse<List<LookupHistory>> response = productLookupService.getLookupHistoryByUserId(userId);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/{userId}/history/{upcCode}")
    public ResponseEntity<ApiResponse<Void>> deleteLookupHistory(
            @PathVariable String userId,
            @PathVariable String upcCode) {
        
        ApiResponse<Void> response = productLookupService.deleteLookupHistory(userId, upcCode);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}