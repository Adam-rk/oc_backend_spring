package com.oc.dto;

public class LoginResponse {
    private String token;

    // Default constructor
    public LoginResponse() {
    }

    // Constructor with parameters
    public LoginResponse(String token) {
        this.token = token;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
