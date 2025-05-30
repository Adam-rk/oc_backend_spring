package com.oc.dto;

/**
 * DTO for receiving message data from clients
 */
public class MessageRequest {
    private String message;
    private Integer user_id;
    private Integer rental_id;

    // Default constructor
    public MessageRequest() {
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getRental_id() {
        return rental_id;
    }

    public void setRental_id(Integer rental_id) {
        this.rental_id = rental_id;
    }
}
