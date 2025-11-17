package com.lsi_project.app.user_microservice.entities;

import com.lsi_project.app.user_microservice.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_address", nullable = false, unique = true, length = 42)
    private String walletAddress;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;



    // --- Relationships ---"


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private AuthenticationState authState;


    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;

    @Transient
    public Set<RoleEnum> getRoles() {
        if (userRoles == null) {
            return Set.of();
        }
        return userRoles.stream()
                .map(userRole -> userRole.getRole().getRoleName())
                .collect(Collectors.toSet());
    }

}