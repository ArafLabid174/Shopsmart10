package com.shopsmart.shopsmart_server.dto;

public class UpdateUserRequest {
    private String fullName;
    private String currentPassword;
    private String newPassword;
    
    // Default constructor
    public UpdateUserRequest() {
    }
    
    // Constructor with fields
    public UpdateUserRequest(String fullName, String currentPassword, String newPassword) {
        this.fullName = fullName;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}