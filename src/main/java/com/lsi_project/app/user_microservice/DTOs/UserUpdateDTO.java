package com.lsi_project.app.user_microservice.DTOs;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @Email(message = "Email should be valid")
    private String email; // Mutable

    private String firstName;
    private String lastName;

}