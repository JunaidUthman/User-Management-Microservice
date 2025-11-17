package com.lsi_project.app.user_microservice.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for the initial request to get a Nonce (Step 1).
 */
@Data
public class NonceRequest {
    @NotBlank
    private String walletAddress;
}
