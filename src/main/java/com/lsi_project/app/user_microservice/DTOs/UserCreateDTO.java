package com.lsi_project.app.user_microservice.DTOs;

import com.lsi_project.app.user_microservice.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

/**
 * DTO used for creating a new User via POST requests.
 * All core fields are mandatory. Roles are optional and used only on creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    @NotBlank(message = "Wallet address is mandatory")
    @Size(min = 42, max = 42, message = "Wallet address must be 42 characters long")
    private String walletAddress;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    // Optional for creation, allows specifying initial roles.
    private Set<RoleEnum> roles;

}