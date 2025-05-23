package com.oc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oc.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    // Default constructor
    public UserResponse() {
    }
    
    // Constructor from User entity
    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        
        if (user.getCreatedAt() != null) {
            this.createdAt = user.getCreatedAt().format(formatter);
        }
        
        if (user.getUpdatedAt() != null) {
            this.updatedAt = user.getUpdatedAt().format(formatter);
        }
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
