package com.oc.controller;

import com.oc.dto.RentalDTO;
import com.oc.dto.RentalRequest;
import com.oc.dto.RentalResponse;
import com.oc.dto.RentalsResponse;
import com.oc.model.Rental;
import com.oc.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oc.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.oc.model.User;

@RestController
@RequestMapping("/api")
public class RentalController {

    @Autowired
    private RentalService rentalService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new rental
     *
     * @param rentalRequest The rental data from the client
     * @return ResponseEntity with success message or error
     */
    @PostMapping(value = "/rentals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRental(@ModelAttribute RentalRequest rentalRequest) {
        try {
            // Get the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // Find the user in the database
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // Create rental
            Rental rental = rentalService.createRental(rentalRequest, userOptional.get().getId());
            
            // Return success response
            RentalResponse response = new RentalResponse("Rental created !");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating rental: " + e.getMessage());
        }
    }
    
    /**
     * Get all rentals
     *
     * @return ResponseEntity with list of all rentals
     */
    @GetMapping("/rentals")
    public ResponseEntity<?> getAllRentals() {
        try {
            // Get all rentals with DTO conversion from service
            List<RentalDTO> dtos = rentalService.getAllRentals();
            return ResponseEntity.ok(dtos);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving rentals: " + e.getMessage());
        }
    }
    
    /**
     * Get rental by ID
     *
     * @param id The ID of the rental to retrieve
     * @return ResponseEntity with the requested rental
     */
    @GetMapping("/rentals/{id}")
    public ResponseEntity<?> getRentalById(@PathVariable Integer id) {
        try {
            // Get rental by ID from service
            RentalDTO dto = rentalService.getRentalById(id);
            return ResponseEntity.ok(dto);
            
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error retrieving rental: " + e.getMessage());
            }
        }
    }
    
    /**
     * Update an existing rental
     *
     * @param id The ID of the rental to update
     * @param rentalRequest The updated rental data
     * @return ResponseEntity with success message or error
     */
    @PutMapping(value = "/rentals/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRental(@PathVariable Integer id, @ModelAttribute RentalRequest rentalRequest) {
        try {
            // Get the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // Find the user in the database
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // Update rental
            Rental rental = rentalService.updateRental(id, rentalRequest, userOptional.get().getId());
            
            // Return success response
            RentalResponse response = new RentalResponse("Rental updated !");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this rental");
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating rental: " + e.getMessage());
            }
        }
    }
}
