package com.shopsmart.shopsmart_server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.model.LookupHistory;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.LookupHistoryRepository;
import com.shopsmart.shopsmart_server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProductLookupServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private LookupHistoryRepository lookupHistoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductLookupService productLookupService;

    private final String USER_ID = "68210dfeb7f39d47f4437f77";
    private final String UPC_CODE = "761531127634";

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(USER_ID);
    }

    // --- getLookupHistoryByUserId ---

    @Test
    void testGetLookupHistoryByUserId_userNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ApiResponse<List<LookupHistory>> resp = productLookupService.getLookupHistoryByUserId(USER_ID);
        assertFalse(resp.isSuccess());
        assertEquals("User not found", resp.getMessage());
    }

    @Test
    void testGetLookupHistoryByUserId_found() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        LookupHistory history1 = new LookupHistory();
        history1.setUserId(USER_ID);
        history1.setUpcCode(UPC_CODE);
        LookupHistory history2 = new LookupHistory();
        history2.setUserId(USER_ID);
        history2.setUpcCode("OTHER");
        List<LookupHistory> mockList = Arrays.asList(history1, history2);

        when(lookupHistoryRepository.findByUserId(USER_ID)).thenReturn(mockList);

        ApiResponse<List<LookupHistory>> resp = productLookupService.getLookupHistoryByUserId(USER_ID);

        assertTrue(resp.isSuccess());
        assertEquals("Lookup history retrieved successfully", resp.getMessage());
        assertNotNull(resp.getData());
        assertEquals(2, resp.getData().size());
        assertEquals(UPC_CODE, resp.getData().get(0).getUpcCode());
    }

    // --- deleteLookupHistory ---

    @Test
    void testDeleteLookupHistory_userNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ApiResponse<Void> resp = productLookupService.deleteLookupHistory(USER_ID, UPC_CODE);
        assertFalse(resp.isSuccess());
        assertEquals("User not found", resp.getMessage());
        verifyNoInteractions(lookupHistoryRepository);
    }

    @Test
    void testDeleteLookupHistory_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        ApiResponse<Void> resp = productLookupService.deleteLookupHistory(USER_ID, UPC_CODE);
        assertTrue(resp.isSuccess());
        assertEquals("Lookup history deleted successfully", resp.getMessage());
        verify(lookupHistoryRepository, times(1)).deleteByUserIdAndUpcCode(USER_ID, UPC_CODE);
    }

    // --- lookupProductByUpc (main happy path + edge) ---

    @Test
    void testLookupProductByUpc_userNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ApiResponse<JsonNode> resp = productLookupService.lookupProductByUpc(USER_ID, UPC_CODE);
        assertFalse(resp.isSuccess());
        assertEquals("User not found", resp.getMessage());
    }

    @Test
    void testLookupProductByUpc_productNotFound() throws Exception {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        String lookupUrl = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + UPC_CODE;

        String fakeResponse = "{\"code\":\"OK\",\"total\":0,\"items\":[]}";
        when(restTemplate.getForObject(lookupUrl, String.class)).thenReturn(fakeResponse);

        // --- mock behavior for objectMapper and JsonNode structure ---
        JsonNode rootNode = mock(JsonNode.class);

        JsonNode codeNode = mock(JsonNode.class);
        when(codeNode.asText()).thenReturn("OK");
        when(rootNode.path("code")).thenReturn(codeNode);

        JsonNode totalNode = mock(JsonNode.class);
        when(totalNode.asInt()).thenReturn(0);
        when(rootNode.path("total")).thenReturn(totalNode);

        when(objectMapper.readTree(fakeResponse)).thenReturn(rootNode);

        // --- actual service call and assertions ---
        ApiResponse<JsonNode> resp = productLookupService.lookupProductByUpc(USER_ID, UPC_CODE);

        assertFalse(resp.isSuccess());
        assertEquals("Product not found for UPC code: " + UPC_CODE, resp.getMessage());
    }

    @Test
    void testLookupProductByUpc_apiError() throws Exception {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        String lookupUrl = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + UPC_CODE;

        when(restTemplate.getForObject(lookupUrl, String.class)).thenThrow(new RuntimeException("API failed"));
        ApiResponse<JsonNode> resp = productLookupService.lookupProductByUpc(USER_ID, UPC_CODE);
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("Error looking up product"));
    }

    @Test
    void testLookupProductByUpc_success() throws Exception {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        String lookupUrl = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + UPC_CODE;

        String responseJson = "{\"code\":\"OK\",\"total\":1,\"items\":[{\"title\":\"Test Title\",\"description\":\"desc\",\"brand\":\"BrandX\","
                + "\"offers\":[{\"price\":123.45,\"link\":\"plink\"}],"
                + "\"images\":[\"myimg\"]}]}";
        when(restTemplate.getForObject(lookupUrl, String.class)).thenReturn(responseJson);

        ObjectMapper realMapper = new ObjectMapper();
        JsonNode rootNode = realMapper.readTree(responseJson);
        when(objectMapper.readTree(responseJson)).thenReturn(rootNode);

        // Save history stub
        when(lookupHistoryRepository.save(any(LookupHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<JsonNode> resp = productLookupService.lookupProductByUpc(USER_ID, UPC_CODE);

        assertTrue(resp.isSuccess());
        assertEquals("Product found", resp.getMessage());
        assertNotNull(resp.getData());
        // verify history saved
        ArgumentCaptor<LookupHistory> cap = ArgumentCaptor.forClass(LookupHistory.class);
        verify(lookupHistoryRepository).save(cap.capture());
        LookupHistory historyRec = cap.getValue();
        assertEquals(USER_ID, historyRec.getUserId());
        assertEquals(UPC_CODE, historyRec.getUpcCode());
        assertEquals(lookupUrl, historyRec.getLookupUrl());
        assertEquals("Test Title", historyRec.getProductTitle());
        assertEquals("desc", historyRec.getProductDescription());
        assertEquals("BrandX", historyRec.getProductBrand());
        assertEquals(123.45, historyRec.getProductPrice());
        assertEquals("plink", historyRec.getProductLink());
        assertEquals("myimg", historyRec.getImageLink());
    }

    // ----- helpers -----
    private JsonNode mockIntNode(int val) {
        JsonNode node = mock(JsonNode.class);
        when(node.asInt()).thenReturn(val);
        return node;
    }

    private JsonNode mockTextNode(String val) {
        JsonNode node = mock(JsonNode.class);
        when(node.asText()).thenReturn(val);
        return node;
    }
}