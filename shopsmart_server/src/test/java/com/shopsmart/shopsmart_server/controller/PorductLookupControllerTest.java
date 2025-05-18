package com.shopsmart.shopsmart_server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.dto.UpcLookupRequest;
import com.shopsmart.shopsmart_server.model.LookupHistory;
import com.shopsmart.shopsmart_server.service.ProductLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProductLookupControllerTest {

    @InjectMocks
    private ProductLookupController productLookupController;

    @Mock
    private ProductLookupService productLookupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void lookupProductByUpc_shouldReturnBadRequest_whenUpcCodeIsNullOrEmpty() {
        // Arrange
        String userId = "testUserId";
        UpcLookupRequest request = new UpcLookupRequest();
        request.setUpcCode(""); // Empty UPC code

        // Act
        ResponseEntity<ApiResponse<JsonNode>> responseEntity = productLookupController.lookupProductByUpc(userId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("UPC code is required", responseEntity.getBody().getMessage());
    }

    @Test
    void lookupProductByUpc_shouldReturnOk_whenLookupSucceeds() {
        // Arrange
        String userId = "testUserId";
        String upcCode = "123456789";
        JsonNode mockProductInfo = null; // Mock JSON payload
        ApiResponse<JsonNode> apiResponse = new ApiResponse<>(true, "Lookup successful", mockProductInfo);
        
        UpcLookupRequest request = new UpcLookupRequest();
        request.setUpcCode(upcCode);

        when(productLookupService.lookupProductByUpc(eq(userId), eq(upcCode))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<JsonNode>> responseEntity = productLookupController.lookupProductByUpc(userId, request);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void lookupProductByUpc_shouldReturnBadRequest_whenLookupFails() {
        // Arrange
        String userId = "testUserId";
        String upcCode = "123456789";
        ApiResponse<JsonNode> apiResponse = new ApiResponse<>(false, "Lookup failed", null);
        
        UpcLookupRequest request = new UpcLookupRequest();
        request.setUpcCode(upcCode);

        when(productLookupService.lookupProductByUpc(eq(userId), eq(upcCode))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<JsonNode>> responseEntity = productLookupController.lookupProductByUpc(userId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void getLookupHistory_shouldReturnOk_whenRetrievalSucceeds() {
        // Arrange
        String userId = "testUserId";
        List<LookupHistory> mockHistoryList = Collections.singletonList(new LookupHistory());
        ApiResponse<List<LookupHistory>> apiResponse = new ApiResponse<>(true, "History retrieval successful", mockHistoryList);

        when(productLookupService.getLookupHistoryByUserId(eq(userId))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<List<LookupHistory>>> responseEntity = productLookupController.getLookupHistory(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void getLookupHistory_shouldReturnBadRequest_whenRetrievalFails() {
        // Arrange
        String userId = "testUserId";
        ApiResponse<List<LookupHistory>> apiResponse = new ApiResponse<>(false, "History retrieval failed", null);

        when(productLookupService.getLookupHistoryByUserId(eq(userId))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<List<LookupHistory>>> responseEntity = productLookupController.getLookupHistory(userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void deleteLookupHistory_shouldReturnOk_whenDeletionSucceeds() {
        // Arrange
        String userId = "testUserId";
        String upcCode = "123456789";
        ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Deletion successful", null);

        when(productLookupService.deleteLookupHistory(eq(userId), eq(upcCode))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> responseEntity = productLookupController.deleteLookupHistory(userId, upcCode);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void deleteLookupHistory_shouldReturnBadRequest_whenDeletionFails() {
        // Arrange
        String userId = "testUserId";
        String upcCode = "123456789";
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Deletion failed", null);

        when(productLookupService.deleteLookupHistory(eq(userId), eq(upcCode))).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> responseEntity = productLookupController.deleteLookupHistory(userId, upcCode);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }
}