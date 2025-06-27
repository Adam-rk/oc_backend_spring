package com.oc.controller;

import com.oc.dto.MessageRequest;
import com.oc.dto.MessageResponse;
import com.oc.model.Message;
import com.oc.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Create a new message
     * 
     * @param messageRequest The message data from the client
     * @return ResponseEntity with success message or error
     */
    @Operation(summary = "Create a new message", description = "Creates a new message for a rental property")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message sent successfully", 
                content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "User or rental not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(
            @Parameter(description = "Message data") @RequestBody MessageRequest messageRequest) {
        try {
            // Validate user exists
            if (!messageService.userExists(messageRequest.getUser_id())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
            }
            
            // Validate rental exists
            if (!messageService.rentalExists(messageRequest.getRental_id())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rental not found");
            }
            
            // Create message
            Message message = messageService.createMessage(messageRequest);
            
            // Return success response
            MessageResponse response = new MessageResponse("Message send with success");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating message: " + e.getMessage());
        }
    }
}
