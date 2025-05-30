package com.oc.service;

import com.oc.dto.MessageRequest;
import com.oc.model.Message;
import com.oc.model.Rental;
import com.oc.model.User;
import com.oc.repository.MessageRepository;
import com.oc.repository.RentalRepository;
import com.oc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * Create a new message
     * 
     * @param messageRequest The message data from the client
     * @return The created message or null if user or rental not found
     * @throws Exception if there's an error during message creation
     */
    public Message createMessage(MessageRequest messageRequest) throws Exception {
        // Validate user exists
        Optional<User> userOptional = userRepository.findById(messageRequest.getUser_id());
        if (!userOptional.isPresent()) {
            return null;
        }

        // Validate rental exists
        Optional<Rental> rentalOptional = rentalRepository.findById(messageRequest.getRental_id());
        if (!rentalOptional.isPresent()) {
            return null;
        }

        // Create new message
        Message message = new Message();
        message.setMessage(messageRequest.getMessage());
        message.setUser(userOptional.get());
        message.setRental(rentalOptional.get());
        
        // Set creation and update timestamps
        LocalDateTime now = LocalDateTime.now();
        message.setCreatedAt(now);
        message.setUpdatedAt(now);
        
        // Save message to database
        return messageRepository.save(message);
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
    
    /**
     * Check if a rental exists
     * 
     * @param rentalId The rental ID to check
     * @return true if rental exists, false otherwise
     */
    public boolean rentalExists(Integer rentalId) {
        return rentalRepository.findById(rentalId).isPresent();
    }
}
