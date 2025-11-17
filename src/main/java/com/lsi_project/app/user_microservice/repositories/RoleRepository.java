package com.lsi_project.app.user_microservice.repositories;

import com.lsi_project.app.user_microservice.entities.Role;
import com.lsi_project.app.user_microservice.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(RoleEnum roleName);
}
