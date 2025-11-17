package com.lsi_project.app.user_microservice.configuration;


import com.lsi_project.app.user_microservice.entities.Role;
import com.lsi_project.app.user_microservice.enums.RoleEnum;
import com.lsi_project.app.user_microservice.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@AllArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;

    @Bean
    @Transactional
    public CommandLineRunner initRoles() {
        return args -> {
            List<RoleEnum> staticRoles = Arrays.asList(RoleEnum.OWNER, RoleEnum.ADMIN, RoleEnum.TENANT);

            for (RoleEnum roleEnum : staticRoles) {
                // just checking if the role already exists
                Optional<Role> existingRole = roleRepository.findByRoleName(roleEnum);

                if (existingRole.isEmpty()) {
                    // 2. Create the entity instance
                    Role newRole = new Role();
                    newRole.setRoleName(roleEnum);

                    // 3. Persist the entity to the database
                    roleRepository.save(newRole);
                    System.out.println("Initialized static role: " + roleEnum.name());
                } else {
                    System.out.println("Role already exists: " + roleEnum.name());
                }
            }
        };
    }
}
