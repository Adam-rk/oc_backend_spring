package com.oc.dto;

import java.util.List;

/**
 * DTO for sending a list of rentals to clients
 */
public class RentalsResponse {
    private List<RentalDTO> rentals;
    
    // Default constructor
    public RentalsResponse() {
    }
    
    // Constructor with rentals
    public RentalsResponse(List<RentalDTO> rentals) {
        this.rentals = rentals;
    }
    
    // Getters and setters
    public List<RentalDTO> getRentals() {
        return rentals;
    }
    
    public void setRentals(List<RentalDTO> rentals) {
        this.rentals = rentals;
    }
}
