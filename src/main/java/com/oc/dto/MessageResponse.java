package com.oc.dto;

/**
 * DTO for sending message response to clients
 */
public class MessageResponse {
    private String message;

    // Default constructor
    public MessageResponse() {
    }

    // Constructor with message
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
