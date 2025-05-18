package com.shopsmart.shopsmart_server.controller;

import com.shopsmart.shopsmart_server.dto.SignInRequest;
import com.shopsmart.shopsmart_server.dto.SignupRequest;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Clean and set up test users, etc.
        userRepository.deleteAll();
    }

    @Test
    void whenValidSignupRequest_thenShouldCreateUser() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFullName("John Doe");
        signupRequest.setEmail("john.doe@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/signup", new HttpEntity<>(signupRequest), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userRepository.existsByEmail("john.doe@example.com")).isTrue();
    }


    @Test
    void whenValidSignInRequest_thenShouldAuthenticateUser() {
        // Register a user with an already-hashed password
        userRepository.save(new User("John Doe", "john.doe@example.com", passwordEncoder.encode("password123")));

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("john.doe@example.com");
        signInRequest.setPassword("password123");

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/signin",
                new HttpEntity<>(signInRequest),
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}