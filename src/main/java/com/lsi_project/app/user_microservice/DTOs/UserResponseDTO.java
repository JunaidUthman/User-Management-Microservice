package com.lsi_project.app.user_microservice.DTOs;

import com.lsi_project.app.user_microservice.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

/**
 * DTO used for returning User data in GET requests.
 * It flattens the 'UserRole' relationship into a simple list of RoleEnums.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String walletAddress;
    private String email;
    private String firstName;
    private String lastName;

    // The flattened set of roles for easy consumption by the client
    private Set<RoleEnum> roles;

    // Optional: Include the authentication state if required for the API
    private AuthenticationStateDTO authState;

}