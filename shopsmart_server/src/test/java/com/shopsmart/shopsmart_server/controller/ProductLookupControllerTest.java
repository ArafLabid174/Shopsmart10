package com.shopsmart.shopsmart_server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductLookupControllerTest {

    @InjectMocks
    private ProductLookupController productLookupController;

    @Mock
    private ProductLookupService productLookupService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void lookupProductByUpc_shouldReturnOkStatus_whenLookupSucceeds() throws Exception {
        // Arrange
        String userId = "68210dfeb7f39d47f4437f77";
        UpcLookupRequest request = new UpcLookupRequest();
        request.setUpcCode("761531127634");

        JsonNode mockProductData = objectMapper.createObjectNode().put("code", "OK")
                .put("total", 1).put("offset", 0);
        ((ObjectNode) mockProductData).putArray("items").add(objectMapper.createObjectNode()
                .put("ean", "0761531127634")
                .put("title", "Used Apple MacBook 12 Laptop with Retina Display (Silver 8GB RAM, 256 GB SSD) (Scratches)")
                .put("brand", "Apple")
                .putArray("images").add("https://i5.walmartimages.com/asr/65d15e64-59a3-41ae-91ce-1f573b240c5a.2c69475aa86e45f69584c2216b659862.jpeg?odnHeight=450&odnWidth=450&odnBg=ffffff"));

        ApiResponse<JsonNode> apiResponse = new ApiResponse<>(true, "Product found", mockProductData);

        when(productLookupService.lookupProductByUpc(anyString(), anyString())).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<JsonNode>> responseEntity = productLookupController.lookupProductByUpc(userId, request);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void getLookupHistory_shouldReturnOkStatus_whenHistoryRetrievedSuccessfully() {
        // Arrange
        String userId = "68210dfeb7f39d47f4437f77";
        List<LookupHistory> mockHistory = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            LookupHistory history = new LookupHistory();
            history.setId("dummyId" + i);
            history.setUserId(userId);
            history.setUpcCode("761531127634");
            history.setLookupUrl("https://api.upcitemdb.com/prod/trial/lookup?upc=761531127634");
            history.setProductTitle("Used Apple MacBook 12 Laptop with Retina Display (Silver 8GB RAM, 256 GB SSD) (Scratches)");
            history.setProductBrand("Apple");
            history.setProductPrice(269.97);
            mockHistory.add(history);
        }

        ApiResponse<List<LookupHistory>> apiResponse = new ApiResponse<>(true, "Lookup history retrieved successfully", mockHistory);

        when(productLookupService.getLookupHistoryByUserId(anyString())).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<List<LookupHistory>>> responseEntity = productLookupController.getLookupHistory(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void deleteLookupHistory_shouldReturnOkStatus_whenDeletionSucceeds() {
        // Arrange
        String userId = "68210dfeb7f39d47f4437f77";
        String upcCode = "761531127634";
        ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Lookup history deleted successfully", null);

        when(productLookupService.deleteLookupHistory(anyString(), anyString())).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> responseEntity = productLookupController.deleteLookupHistory(userId, upcCode);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }
}