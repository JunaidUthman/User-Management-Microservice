package com.lsi_project.app.user_microservice.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for AuthenticationState entity. Used for communicating the current
 * security state (like a nonce) without exposing the internal user ID.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationStateDTO {

    private String currentNonce;
    private LocalDateTime nonceGeneratedAt;

}