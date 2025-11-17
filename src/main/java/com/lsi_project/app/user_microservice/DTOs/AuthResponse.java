package com.lsi_project.app.user_microservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for sending the JWT back to the client upon successful authentication.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String walletAddress;
}