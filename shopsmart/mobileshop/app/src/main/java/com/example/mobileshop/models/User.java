package com.example.mobileshop.models;

import java.util.List;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private List<Integer> createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(List<Integer> createdAt) {
        this.createdAt = createdAt;
    }
}
