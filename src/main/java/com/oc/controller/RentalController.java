package com.oc.controller;

import com.oc.dto.RentalDTO;
import com.oc.dto.RentalRequest;
import com.oc.dto.RentalResponse;
import com.oc.dto.RentalsResponse;
import com.oc.model.Rental;
import com.oc.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oc.repository.UserRepository;
import com.oc.service.FileStorageService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.oc.model.User;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Create a new rental
     *
     * @param rentalRequest The rental data from the client
     * @return ResponseEntity with success message or error
     */
    @Operation(summary = "Create a new rental", description = "Creates a new rental property with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental created successfully",
                content = @Content(schema = @Schema(implementation = RentalResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
            rentalService.createRental(rentalRequest, userOptional.get().getId());

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
    @Operation(summary = "Get all rentals", description = "Retrieves a list of all rental properties")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of rentals retrieved successfully",
                content = @Content(schema = @Schema(implementation = RentalsResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rentals")
    public ResponseEntity<?> getAllRentals() {
        try {
            // Get all rentals with DTO conversion from service
            List<RentalDTO> dtos = rentalService.getAllRentals();
            return ResponseEntity.ok(new RentalsResponse(dtos));

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
    @Operation(summary = "Get rental by ID", description = "Retrieves a specific rental by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental retrieved successfully",
                content = @Content(schema = @Schema(implementation = RentalDTO.class))),
        @ApiResponse(responseCode = "404", description = "Rental not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rentals/{id}")
    public ResponseEntity<?> getRentalById(
            @Parameter(description = "ID of the rental to retrieve") @PathVariable Integer id) {
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
     * @param id            The ID of the rental to update
     * @param rentalRequest The updated rental data
     * @return ResponseEntity with success message or error
     */
    @Operation(summary = "Update an existing rental", description = "Updates a rental property with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental updated successfully",
                content = @Content(schema = @Schema(implementation = RentalResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this rental"),
        @ApiResponse(responseCode = "404", description = "Rental not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/rentals/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRental(
            @Parameter(description = "ID of the rental to update") @PathVariable Integer id, 
            @Parameter(description = "Updated rental data") @ModelAttribute RentalRequest rentalRequest) {
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
    
    /**
     * Get a file by its filename
     * 
     * @param filename The name of the file to retrieve
     * @return ResponseEntity with the file as a resource
     */
    @Operation(summary = "Get a file by filename", description = "Retrieves a file (image) by its filename")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File retrieved successfully",
                content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/rentals/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(
            @Parameter(description = "Name of the file to retrieve") @PathVariable String filename) {
        try {
            // Load file as resource
            Resource resource = fileStorageService.loadFileAsResource(filename);

            // Try to determine file's content type
            String contentType = "application/octet-stream";
            
            // Fallback to the default content type if type could not be determined
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filename.endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.endsWith(".gif")) {
                contentType = "image/gif";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            
        } catch (IOException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
