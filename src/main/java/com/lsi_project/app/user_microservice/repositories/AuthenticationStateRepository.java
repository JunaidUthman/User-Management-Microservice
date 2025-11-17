package com.lsi_project.app.user_microservice.repositories;

import com.lsi_project.app.user_microservice.entities.AuthenticationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationStateRepository extends JpaRepository<AuthenticationState,Long> {
    Optional<AuthenticationState> findByCurrentNonce(String nonce);
}
