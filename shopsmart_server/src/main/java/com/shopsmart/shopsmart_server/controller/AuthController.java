package com.shopsmart.shopsmart_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsmart.shopsmart_server.dto.ApiResponse;
import com.shopsmart.shopsmart_server.dto.SignInRequest;
import com.shopsmart.shopsmart_server.dto.SignupRequest;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")  // Allow requests from any origin for development
public class AuthController {

    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody SignupRequest signupRequest) {
        ApiResponse<User> response = userService.registerUser(signupRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<User>> signIn(@RequestBody SignInRequest signInRequest) {
        ApiResponse<User> response = userService.signIn(signInRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}