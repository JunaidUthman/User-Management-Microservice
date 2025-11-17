package com.lsi_project.app.user_microservice.services;

import com.lsi_project.app.user_microservice.DTOs.UserCreateDTO;
import com.lsi_project.app.user_microservice.DTOs.UserResponseDTO;
import com.lsi_project.app.user_microservice.DTOs.UserUpdateDTO;
import com.lsi_project.app.user_microservice.entities.AuthenticationState;
import com.lsi_project.app.user_microservice.entities.Role;
import com.lsi_project.app.user_microservice.entities.User;
import com.lsi_project.app.user_microservice.entities.UserRole;
import com.lsi_project.app.user_microservice.enums.RoleEnum;
import com.lsi_project.app.user_microservice.repositories.RoleRepository;
import com.lsi_project.app.user_microservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Default role assigned to new users (e.g., TENANT)
    @Value("${app.default-role:TENANT}")
    private RoleEnum defaultRole;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // --- MAPPING UTILITY ---

    private UserResponseDTO toDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getWalletAddress(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(), // Uses the convenient @Transient method
                null // authState is set to null for now
        );
    }

    // --- CRUD OPERATIONS ---

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID: "
                ));
        return toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByWalletAddress(String walletAddress) {
        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID: "
                ));
        return toDTO(user);
    }

    /**
     * Creates a new User from a DTO, assigns roles (either explicitly or default),
     * and creates an initial Nonce.
     * @param createDTO The UserCreateDTO containing user details.
     * @return UserResponseDTO of the newly created user.
     */
    @Transactional
    public UserResponseDTO create(UserCreateDTO createDTO) {

        // 1. Validation check
        if (userRepository.existsByWalletAddress(createDTO.getWalletAddress()) ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Wallet address is already registered: " + createDTO.getWalletAddress()
            );
        } else if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "email address is already exists: " + createDTO.getWalletAddress()
            );

        }


        // 2. Create User Entity
        User user = new User();
        user.setWalletAddress(createDTO.getWalletAddress());
        user.setEmail(createDTO.getEmail());
        user.setFirstName(createDTO.getFirstName());
        user.setLastName(createDTO.getLastName());

        // 3. Persist User to get ID (required for OneToOne mapping)
        user = userRepository.save(user);

        // 4. Determine Roles to Assign
        Set<RoleEnum> rolesToAssign = createDTO.getRoles() != null && !createDTO.getRoles().isEmpty()
                ? createDTO.getRoles()
                : Set.of(defaultRole);

        Set<UserRole> userRoles = new HashSet<>();
        for (RoleEnum roleEnum : rolesToAssign) {
            Role roleEntity = roleRepository.findByRoleName(roleEnum)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + roleEnum.name()));
            userRoles.add(new UserRole(user, roleEntity));
        }

        user.setUserRoles(userRoles);

        // 5. Create Initial Authentication State (e.g., generate a Nonce)
        AuthenticationState authState = new AuthenticationState();
        authState.setUser(user);
        authState.setCurrentNonce(null); // its null because the creation of the nonce logique should be implemented on the login process
        user.setAuthState(authState);

        // 6. Save the user (CascadeType.ALL ensures UserRole and AuthState are saved)
        user = userRepository.save(user);

        return toDTO(user);
    }

    /**
     * Updates an existing User's details using a DTO.
     * NOTE: This method relies on UserUpdateDTO which naturally excludes the immutable walletAddress.
     * @param id The ID of the user to update.
     * @param updateDTO The DTO containing fields to update.
     * @return UserResponseDTO of the updated user.
     */
    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found with ID: "));

        // Update fields based on DTO (UserUpdateDTO only contains mutable fields)

        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }

        // These fields are @NotBlank in the DTO, so they should be present if validation passed.
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());

        // Save and return the updated DTO
        user = userRepository.save(user);
        return toDTO(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found with ID: ");
        }
        userRepository.deleteById(id);
    }
}