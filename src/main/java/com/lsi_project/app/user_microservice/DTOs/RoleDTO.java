package com.lsi_project.app.user_microservice.DTOs;

import com.lsi_project.app.user_microservice.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for the Role Entity. Used primarily for representing a role name in a clean,
 * non-entity-dependent structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {

    private RoleEnum roleName;

}