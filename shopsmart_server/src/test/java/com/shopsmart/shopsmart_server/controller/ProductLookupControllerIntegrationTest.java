package com.shopsmart.shopsmart_server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.dto.UpcLookupRequest;
import com.shopsmart.shopsmart_server.model.LookupHistory;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.LookupHistoryRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductLookupControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;           // For calling YOUR controllers in test

    @MockBean
    private RestTemplate restTemplateMock;           // For mocking external API calls in service

    @Autowired
    private LookupHistoryRepository lookupHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId("user123");
        user.setEmail("testuser@example.com");
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        lookupHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenValidUpcLookup_thenShouldReturnProduct() {
        // Arrange
        String userId = "user123";
        String upcCode = "123456789012";
        UpcLookupRequest request = new UpcLookupRequest();
        request.setUpcCode(upcCode);

        // Arrange mock for external UPC API (this is injected into your ProductLookupService!)
        String mockApiJson = "{ \"code\": \"OK\", \"total\": 1, \"items\": [ { \"title\": \"Mock Product\", \"brand\": \"Mock Brand\", \"images\": [\"http://example.com/mock-image.jpg\"], \"offers\": [{}] } ] }";
        String externalUrl = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + upcCode;
        when(restTemplateMock.getForObject(externalUrl, String.class)).thenReturn(mockApiJson);

        // Act
        ResponseEntity<ApiResponse<JsonNode>> response = restTemplate.exchange(
                "/api/product/" + userId + "/lookup",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<JsonNode>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<JsonNode> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        JsonNode dataNode = responseBody.getData();
        assertThat(dataNode).isNotNull();
        assertThat(dataNode.path("items").get(0).path("title").asText()).isEqualTo("Mock Product");

        // Verify history saved to repository
        List<LookupHistory> histories = lookupHistoryRepository.findByUserId(userId);
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getUpcCode()).isEqualTo(upcCode);
    }

    @Test
    void whenValidUserId_thenShouldReturnLookupHistory() {
        // Arrange
        LookupHistory history = new LookupHistory(
                "user123",
                "123456789012",
                "http://example.com",
                "Test Product",
                "This is a test description",
                "Test Brand",
                12.99,
                "http://example.com/product",
                "http://example.com/image.jpg"
        );
        lookupHistoryRepository.save(history);

        // Act
        ResponseEntity<ApiResponse<List<LookupHistory>>> response =
                restTemplate.exchange(
                        "/api/product/user123/history",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ApiResponse<List<LookupHistory>>>() {}
                );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<List<LookupHistory>> responseBody = response.getBody();
        assertThat(responseBody.getData()).hasSize(1);
        assertThat(responseBody.getData().get(0).getUpcCode()).isEqualTo("123456789012");
    }

    @Test
    void whenValidDeleteRequest_thenShouldRemoveHistory() {
        // Arrange
        LookupHistory history = new LookupHistory(
                "user123",
                "123456789012",
                "http://example.com",
                "Test Product",
                "This is a test description",
                "Test Brand",
                12.99,
                "http://example.com/product",
                "http://example.com/image.jpg"
        );
        lookupHistoryRepository.save(history);

        // Act
        ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                "/api/product/user123/history/123456789012",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<ApiResponse<Void>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(lookupHistoryRepository.findByUserId("user123")).isEmpty();
    }
}