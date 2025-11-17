package com.lsi_project.app.user_microservice.repositories;


import com.lsi_project.app.user_microservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByWalletAddress(String walletAddress);

    // Check if a wallet address already exists
    boolean existsByWalletAddress(String walletAddress);
    boolean existsByEmail(String walletAddress);
}
