package com.shopsmart.shopsmart_server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.dto.SignInRequest;
import com.shopsmart.shopsmart_server.dto.SignupRequest;
import com.shopsmart.shopsmart_server.dto.UpdateUserRequest;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public ApiResponse<User> registerUser(SignupRequest signupRequest) {
        // Validate request
        if (signupRequest.getFullName() == null || signupRequest.getFullName().trim().isEmpty()) {
            return ApiResponse.error("Full name is required");
        }
        
        if (signupRequest.getEmail() == null || signupRequest.getEmail().trim().isEmpty()) {
            return ApiResponse.error("Email is required");
        }
        
        if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
            return ApiResponse.error("Password is required");
        }
        
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            return ApiResponse.error("Passwords do not match");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ApiResponse.error("Email is already registered");
        }
        
        // Create new user with hashed password
        User user = new User(
            signupRequest.getFullName(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword())  // Hash the password using BCrypt
        );
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        // Don't return the hashed password in the response
        savedUser.setPassword(null);
        // Return success response
        System.out.println("");
        return ApiResponse.success("User registered successfully", savedUser);
    }
    
    public ApiResponse<User> signIn(SignInRequest signInRequest) {
        // Validate request
        if (signInRequest.getEmail() == null || signInRequest.getEmail().trim().isEmpty()) {
            return ApiResponse.error("Email is required");
        }
        
        if (signInRequest.getPassword() == null || signInRequest.getPassword().trim().isEmpty()) {
            return ApiResponse.error("Password is required");
        }
        
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(signInRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            return ApiResponse.error("Invalid credentials");
        }
        
        User user = userOptional.get();
        
        // Check if password matches
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            return ApiResponse.error("Invalid credentials");
        }
        
        // Create a copy of the user without password for the response
        User userResponse = new User(
            user.getFullName(),
            user.getEmail(),
            null  // Don't include password in response
        );
        userResponse.setId(user.getId());
        userResponse.setCreatedAt(user.getCreatedAt());
        
        return ApiResponse.success("Sign in successful", userResponse);
    }

    public ApiResponse<User> updateUser(String userId, UpdateUserRequest updateRequest) {
        // Validate request
        if (updateRequest.getCurrentPassword() == null || updateRequest.getCurrentPassword().trim().isEmpty()) {
            return ApiResponse.error("Current password is required");
        }
        
        // Find user by ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        
        User user = userOptional.get();
        
        // Verify current password
        if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
            return ApiResponse.error("Current password is incorrect");
        }
        
        // Update name if provided
        if (updateRequest.getFullName() != null && !updateRequest.getFullName().trim().isEmpty()) {
            user.setFullName(updateRequest.getFullName().trim());
        }
        
        // Update password if new password is provided
        if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }
        
        // Save updated user
        User updatedUser = userRepository.save(user);
        
        // Create response without password
        User userResponse = new User(
            updatedUser.getFullName(),
            updatedUser.getEmail(),
            null  // Don't include password in response
        );
        userResponse.setId(updatedUser.getId());
        userResponse.setCreatedAt(updatedUser.getCreatedAt());
        
        return ApiResponse.success("User updated successfully", userResponse);
    }
}