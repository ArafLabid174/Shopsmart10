<<<<<<< HEAD
package com.shopsmart.shopsmart_server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.LookupHistory;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.LookupHistoryRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;

@Service
public class ProductLookupService {

    private final RestTemplate restTemplate;
    private final LookupHistoryRepository lookupHistoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    private static final String UPC_LOOKUP_BASE_URL = "https://api.upcitemdb.com/prod/trial/lookup?upc=";
    
    @Autowired
    public ProductLookupService(RestTemplate restTemplate, 
                               LookupHistoryRepository lookupHistoryRepository,
                               UserRepository userRepository,
                               ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.lookupHistoryRepository = lookupHistoryRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }
    
    public ApiResponse<JsonNode> lookupProductByUpc(String userId, String upcCode) {
        try {
            // Validate user
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            
            // Build lookup URL
            String lookupUrl = UPC_LOOKUP_BASE_URL + upcCode;
            
            // Call external API
            String responseBody = restTemplate.getForObject(lookupUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            // Check if product was found
            if (!rootNode.path("code").asText().equals("OK") || rootNode.path("total").asInt() == 0) {
                return ApiResponse.error("Product not found for UPC code: " + upcCode);
            }
            
            // Extract product information
            JsonNode itemNode = rootNode.path("items").get(0);
            String productTitle = itemNode.path("title").asText();
            String productDescription = itemNode.path("description").asText();
            String productBrand = itemNode.path("brand").asText();
            
            // Get price and image
            Double productPrice = null;
            String productLink = "";
            String imageLink = "";
            
            if (itemNode.path("offers").size() > 0) {
                JsonNode offer = itemNode.path("offers").get(0);
                if (!offer.path("price").isNull() && !offer.path("price").asText().isEmpty()) {
                    productPrice = offer.path("price").asDouble();
                } else if (!offer.path("list_price").isNull() && !offer.path("list_price").asText().isEmpty()) {
                    productPrice = offer.path("list_price").asDouble();
                }
                productLink = offer.path("link").asText();
            }
            
            if (itemNode.path("images").size() > 0) {
                imageLink = itemNode.path("images").get(0).asText();
            }
            
            // Save to history
            LookupHistory history = new LookupHistory(
                userId,
                upcCode,
                lookupUrl,
                productTitle,
                productDescription,
                productBrand,
                productPrice,
                productLink,
                imageLink
            );
            lookupHistoryRepository.save(history);
            
            return ApiResponse.success("Product found", rootNode);
            
        } catch (Exception e) {
            return ApiResponse.error("Error looking up product: " + e.getMessage());
        }
    }
    
    public ApiResponse<List<LookupHistory>> getLookupHistoryByUserId(String userId) {
        // Validate user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        List<LookupHistory> historyList = lookupHistoryRepository.findByUserId(userId);
        return ApiResponse.success("Lookup history retrieved successfully", historyList);
    }
    
    @Transactional
    public ApiResponse<Void> deleteLookupHistory(String userId, String upcCode) {
        // Validate user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        lookupHistoryRepository.deleteByUserIdAndUpcCode(userId, upcCode);
        return ApiResponse.success("Lookup history deleted successfully", null);
    }
=======
package com.shopsmart.shopsmart_server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.LookupHistory;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.LookupHistoryRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;

@Service
public class ProductLookupService {

    private final RestTemplate restTemplate;
    private final LookupHistoryRepository lookupHistoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    private static final String UPC_LOOKUP_BASE_URL = "https://api.upcitemdb.com/prod/trial/lookup?upc=";
    
    @Autowired
    public ProductLookupService(RestTemplate restTemplate, 
                               LookupHistoryRepository lookupHistoryRepository,
                               UserRepository userRepository,
                               ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.lookupHistoryRepository = lookupHistoryRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }
    
    public ApiResponse<JsonNode> lookupProductByUpc(String userId, String upcCode) {
        try {
            // Validate user
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            
            // Build lookup URL
            String lookupUrl = UPC_LOOKUP_BASE_URL + upcCode;
            
            // Call external API
            String responseBody = restTemplate.getForObject(lookupUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            // Check if product was found
            if (!rootNode.path("code").asText().equals("OK") || rootNode.path("total").asInt() == 0) {
                return ApiResponse.error("Product not found for UPC code: " + upcCode);
            }
            
            // Extract product information
            JsonNode itemNode = rootNode.path("items").get(0);
            String productTitle = itemNode.path("title").asText();
            String productBrand = itemNode.path("brand").asText();
            String productImage = itemNode.path("images").size() > 0 ? 
                                  itemNode.path("images").get(0).asText() : "";
            
            // Get price if available
            Double productPrice = null;
            if (itemNode.path("offers").size() > 0) {
                JsonNode offer = itemNode.path("offers").get(0);
                if (!offer.path("price").isNull() && !offer.path("price").asText().isEmpty()) {
                    productPrice = offer.path("price").asDouble();
                } else if (!offer.path("list_price").isNull() && !offer.path("list_price").asText().isEmpty()) {
                    productPrice = offer.path("list_price").asDouble();
                }
            }
            
            // Save to history
            LookupHistory history = new LookupHistory(
                userId,
                upcCode,
                lookupUrl,
                productTitle,
                productBrand,
                productImage,
                productPrice
            );
            lookupHistoryRepository.save(history);
            
            return ApiResponse.success("Product found", rootNode);
            
        } catch (Exception e) {
            return ApiResponse.error("Error looking up product: " + e.getMessage());
        }
    }
    
    public ApiResponse<List<LookupHistory>> getLookupHistoryByUserId(String userId) {
        // Validate user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        List<LookupHistory> historyList = lookupHistoryRepository.findByUserId(userId);
        return ApiResponse.success("Lookup history retrieved successfully", historyList);
    }
    
    @Transactional
    public ApiResponse<Void> deleteLookupHistory(String userId, String upcCode) {
        // Validate user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        lookupHistoryRepository.deleteByUserIdAndUpcCode(userId, upcCode);
        return ApiResponse.success("Lookup history deleted successfully", null);
    }
>>>>>>> f05afc47162a15a3034e3b01c500459bdef193e5
}