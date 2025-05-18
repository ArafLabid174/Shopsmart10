package com.shopsmart.shopsmart_server.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
@ConfigurationProperties(prefix = "chatgpt.api")
public class ChatGptConfig {
    
    private String baseUrl = "https://api.openai.com/v1/chat/completions";
    @Value("${chatgpt.api.key}") 
    private String key;
    private String model = "gpt-4o-search-preview-2025-03-11";
    private int maxResults = 5;
    private int retryMaxAttempts = 3;
    private long retryBackoff = 1000;
    private int timeout = 30000;
    
    @Bean
    public RestTemplate chatGptRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
    }
    
    // Getters and setters
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getMaxResults() {
        return maxResults;
    }
    
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }
    
    public void setRetryMaxAttempts(int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }
    
    public long getRetryBackoff() {
        return retryBackoff;
    }
    
    public void setRetryBackoff(long retryBackoff) {
        this.retryBackoff = retryBackoff;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}