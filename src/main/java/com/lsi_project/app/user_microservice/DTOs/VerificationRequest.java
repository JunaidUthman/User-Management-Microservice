package com.lsi_project.app.user_microservice.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for the signature verification request (Step 3).
 */
@Data
public class VerificationRequest {
    @NotBlank
    private String walletAddress;
    @NotBlank
    private String signature;
}
