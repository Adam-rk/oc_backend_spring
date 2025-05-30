package com.oc.dto;

/**
 * DTO for sending rental response to clients
 */
public class RentalResponse {
    private String message;

    // Default constructor
    public RentalResponse() {
    }

    // Constructor with message
    public RentalResponse(String message) {
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
