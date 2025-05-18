package com.shopsmart.shopsmart_server.controller;

import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.dto.SignInRequest;
import com.shopsmart.shopsmart_server.dto.SignupRequest;
import com.shopsmart.shopsmart_server.dto.UpdateUserRequest;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_shouldReturnCreatedStatus_whenRegistrationSucceeds() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFullName("Hasan");
        signupRequest.setEmail("hasan1095@gmail.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setConfirmPassword("Password123!");

        User mockUser = new User();
        mockUser.setId("68224414847a464cbd525224");
        mockUser.setFullName("Hasan");
        mockUser.setEmail("hasan1095@gmail.com");
        mockUser.setPassword(null);
        mockUser.setCreatedAt(LocalDateTime.of(2025, 5, 13, 0, 55, 16, 67_000_000));

        ApiResponse<User> apiResponse = new ApiResponse<>(true, "User registered successfully", mockUser);

        when(userService.registerUser(any(SignupRequest.class))).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<User>> responseEntity = authController.registerUser(signupRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void registerUser_shouldReturnBadRequestStatus_whenRegistrationFails() {
        SignupRequest signupRequest = new SignupRequest();
        ApiResponse<User> apiResponse = new ApiResponse<>(false, "Registration failed", null);

        when(userService.registerUser(any(SignupRequest.class))).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<User>> responseEntity = authController.registerUser(signupRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void signIn_shouldReturnOkStatus_whenSignInSucceeds() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("hasan1095@gmail.com");
        signInRequest.setPassword("Password123!");

        User mockUser = new User();
        mockUser.setId("68224414847a464cbd525224");
        mockUser.setFullName("Hasan");
        mockUser.setEmail("hasan1095@gmail.com");
        mockUser.setPassword(null);
        mockUser.setCreatedAt(LocalDateTime.of(2025, 5, 13, 0, 55, 16, 67_000_000));

        ApiResponse<User> apiResponse = new ApiResponse<>(true, "Sign in successful", mockUser);

        when(userService.signIn(any(SignInRequest.class))).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<User>> responseEntity = authController.signIn(signInRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void signIn_shouldReturnUnauthorizedStatus_whenSignInFails() {
        SignInRequest signInRequest = new SignInRequest();
        ApiResponse<User> apiResponse = new ApiResponse<>(false, "Invalid credentials", null);

        when(userService.signIn(any(SignInRequest.class))).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<User>> responseEntity = authController.signIn(signInRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void updateUser_shouldReturnOkStatus_whenUpdateSucceeds() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFullName("Haaaasan");
        updateRequest.setCurrentPassword("newpass123");
        updateRequest.setNewPassword("newpass");

        User mockUser = new User();
        mockUser.setId("6825c8fbf4e409157d95b688");
        mockUser.setFullName("Haaaasan");
        mockUser.setEmail("hasan1@gmail.com");
        mockUser.setPassword(null);
        mockUser.setCreatedAt(LocalDateTime.of(2025, 5, 15, 16, 59, 7, 393_000_000));

        ApiResponse<User> apiResponse = new ApiResponse<>(true, "User updated successfully", mockUser);

        when(userService.updateUser(any(String.class), any(UpdateUserRequest.class))).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<User>> responseEntity = authController.updateUser("6825c8fbf4e409157d95b688", updateRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }

    @Test
    void updateUser_shouldReturnBadRequestStatus_whenUpdateFails() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setCurrentPassword("wrongpassword");

        ApiResponse<User> apiResponse = new ApiResponse<>(false, "Update failed", null);

        when(userService.updateUser(any(String.class), any(UpdateUserRequest.class))).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<User>> responseEntity = authController.updateUser("6825c8fbf4e409157d95b688", updateRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(apiResponse, responseEntity.getBody());
    }
}