package com.lsi_project.app.user_microservice.cotrollers;

import com.lsi_project.app.user_microservice.DTOs.UserResponseDTO;
import com.lsi_project.app.user_microservice.DTOs.UserCreateDTO;
import com.lsi_project.app.user_microservice.DTOs.UserUpdateDTO;
import com.lsi_project.app.user_microservice.services.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users - Retrieves a list of all users.
     * Returns HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id} - Retrieves a single user by ID.
     * Returns HTTP 200 OK. Handled 404 NOT FOUND by GlobalExceptionHandler via NoSuchElementException.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/users/wallet/{walletAddress} - Retrieves a single user by wallet address.
     * Returns HTTP 200 OK. Handled 404 NOT FOUND by GlobalExceptionHandler via NoSuchElementException.
     */
    @GetMapping("/wallet/{walletAddress}")
    public ResponseEntity<UserResponseDTO> getUserByWalletAddress(@PathVariable String walletAddress) {
        UserResponseDTO user = userService.findByWalletAddress(walletAddress);
        return ResponseEntity.ok(user);
    }

    /**
     * PUT /api/users/{id} - Updates an existing user's profile information.
     * Uses UserUpdateDTO for input validation (excluding the wallet address).
     * Returns HTTP 200 OK. Handled 404 NOT FOUND by GlobalExceptionHandler.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserResponseDTO updatedUser = userService.update(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * DELETE /api/users/{id} - Deletes a user by ID.
     * Returns HTTP 204 NO CONTENT upon success. Handled 404 NOT FOUND by GlobalExceptionHandler.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        // Best practice for a successful DELETE is to return 204 No Content
        return ResponseEntity.noContent().build();
    }
}