package com.lsi_project.app.user_microservice.cotrollers;

import com.lsi_project.app.user_microservice.DTOs.*;
import com.lsi_project.app.user_microservice.services.AuthService;
import com.lsi_project.app.user_microservice.services.UserService; // Assuming you have a UserService for registration
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles Web3 authentication (Sign-In with Ethereum).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService; // Placeholder for your existing user registration service


    // --- 3. Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserCreateDTO createDTO) {
        UserResponseDTO newUser = userService.create(createDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }


    // --- 1. Nonce Generation (Step 1 of SIWE) ---
    @PostMapping("/nonce")
    public ResponseEntity<String> getNonce(@RequestBody NonceRequest request) {
        try {
            String nonce = authService.generateNonce(request.getWalletAddress());
            // Frontend receives the nonce and prompts MetaMask to sign it (Step 2)
            return ResponseEntity.ok(nonce);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating nonce: " + e.getMessage());
        }
    }

    // --- 2. Signature Verification and Login (Step 3 & 4 of SIWE) ---
    @PostMapping("/verify")
    public ResponseEntity<?> login(@RequestBody VerificationRequest request) {
        try {
            // Service verifies signature against the stored nonce and issues JWT
            AuthResponse authResponse = authService.verifySignatureAndLogin(request);

            // Send JWT in the response body
            return ResponseEntity.ok(authResponse);

        } catch (RuntimeException e) {
            // Verification failed (e.g., Nonce not found, invalid signature)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


}