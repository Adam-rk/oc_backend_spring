package com.oc.controller;

import com.oc.configuration.JwtUtil;
import com.oc.dto.LoginRequest;
import com.oc.dto.LoginResponse;
import com.oc.dto.RegisterRequest;
import com.oc.dto.UserResponse;
import com.oc.model.User;
import com.oc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );

            // Generate JWT token
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            final String jwt = jwtUtil.generateToken(userDetails);

            // Get user ID
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getLogin());
            if (userOptional.isPresent()) {
                LoginResponse response = new LoginResponse(jwt);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found");
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error: " + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
            }
            
            // Create new user
            User newUser = new User();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setName(registerRequest.getName());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            
            // Set creation and update timestamps
            LocalDateTime now = LocalDateTime.now();
            newUser.setCreatedAt(now);
            newUser.setUpdatedAt(now);
            
            // Save user to database
            userRepository.save(newUser);
            
            // Generate JWT token for the new user
            final UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(newUser.getEmail())
                    .password(newUser.getPassword())
                    .authorities("ROLE_USER")
                    .build();
            
            final String jwt = jwtUtil.generateToken(userDetails);
            
            // Return token
            LoginResponse response = new LoginResponse(jwt);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration error: " + e.getMessage());
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // Get the authenticated user's username (email)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // Find the user in the database
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserResponse userResponse = new UserResponse(user);
                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user data: " + e.getMessage());
        }
    }
}
