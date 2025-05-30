package com.oc.service;

import com.oc.dto.RentalDTO;
import com.oc.dto.RentalRequest;
import com.oc.dto.RentalsResponse;
import com.oc.model.Rental;
import com.oc.model.User;
import com.oc.repository.RentalRepository;
import com.oc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Create a new rental
     *
     * @param rentalRequest The rental data from the client
     * @param userId The ID of the user creating the rental
     * @return The created rental
     * @throws Exception if there's an error during rental creation
     */
    public Rental createRental(RentalRequest rentalRequest, Integer userId) throws Exception {
        // Validate user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new Exception("User not found");
        }

        // Create new rental
        Rental rental = new Rental();
        rental.setName(rentalRequest.getName());
        
        // Parse and set surface
        try {
            rental.setSurface(new BigDecimal(rentalRequest.getSurface()));
        } catch (NumberFormatException e) {
            throw new Exception("Invalid surface value");
        }
        
        // Parse and set price
        try {
            rental.setPrice(new BigDecimal(rentalRequest.getPrice()));
        } catch (NumberFormatException e) {
            throw new Exception("Invalid price value");
        }
        
        rental.setDescription(rentalRequest.getDescription());
        rental.setOwner(userOptional.get());
        
        // Handle picture upload
        MultipartFile pictureFile = rentalRequest.getPicture();
        if (pictureFile != null && !pictureFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(pictureFile);
            rental.setPicture(fileName);
        }
        
        // Set creation and update timestamps
        LocalDateTime now = LocalDateTime.now();
        rental.setCreatedAt(now);
        rental.setUpdatedAt(now);
        
        // Save rental to database
        return rentalRepository.save(rental);
    }
    
    /**
     * Update an existing rental
     *
     * @param rentalId The ID of the rental to update
     * @param rentalRequest The updated rental data
     * @param userId The ID of the user updating the rental
     * @return The updated rental
     * @throws Exception if there's an error during rental update
     */
    public Rental updateRental(Integer rentalId, RentalRequest rentalRequest, Integer userId) throws Exception {
        // Validate user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new Exception("User not found");
        }
        
        // Validate rental exists
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);
        if (!rentalOptional.isPresent()) {
            throw new Exception("Rental not found");
        }
        
        // Check if the user is the owner of the rental
        Rental rental = rentalOptional.get();
        if (!rental.getOwner().getId().equals(userId)) {
            throw new Exception("User is not authorized to update this rental");
        }
        
        // Update rental properties
        if (rentalRequest.getName() != null) {
            rental.setName(rentalRequest.getName());
        }
        
        // Update surface if provided
        if (rentalRequest.getSurface() != null) {
            try {
                rental.setSurface(new BigDecimal(rentalRequest.getSurface()));
            } catch (NumberFormatException e) {
                throw new Exception("Invalid surface value");
            }
        }
        
        // Update price if provided
        if (rentalRequest.getPrice() != null) {
            try {
                rental.setPrice(new BigDecimal(rentalRequest.getPrice()));
            } catch (NumberFormatException e) {
                throw new Exception("Invalid price value");
            }
        }
        
        // Update description if provided
        if (rentalRequest.getDescription() != null) {
            rental.setDescription(rentalRequest.getDescription());
        }
        
        // Handle picture upload if provided
        MultipartFile pictureFile = rentalRequest.getPicture();
        if (pictureFile != null && !pictureFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(pictureFile);
            rental.setPicture(fileName);
        }
        
        // Update the updated_at timestamp
        rental.setUpdatedAt(LocalDateTime.now());
        
        // Save updated rental to database
        return rentalRepository.save(rental);
    }
    
    /**
     * Get all rentals
     *
     * @return  List<RentalDTO> containing all rentals as DTOs
     */
    public List<RentalDTO> getAllRentals() {
        // Get all rentals from repository
        List<Rental> rentals = rentalRepository.findAll();
        
        // Convert to DTOs
        List<RentalDTO> rentalDTOs = rentals.stream()
                .map(rental -> new RentalDTO(rental))
                .collect(Collectors.toList());
        
        return rentalDTOs;
    }
    
    /**
     * Get rental by ID
     *
     * @param id The ID of the rental to retrieve
     * @return RentalDTO for the specified rental
     * @throws Exception if rental not found
     */
    public RentalDTO getRentalById(Integer id) throws Exception {
        // Find rental by ID
        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        
        // Throw exception if not found
        if (!rentalOptional.isPresent()) {
            throw new Exception("Rental not found");
        }
        
        // Convert to DTO and return
        return new RentalDTO(rentalOptional.get());
    }
    
    /**
     * Check if a user exists
     *
     * @param userId The user ID to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(Integer userId) {
        return userRepository.findById(userId).isPresent();
    }
}
