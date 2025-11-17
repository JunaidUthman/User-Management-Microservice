package com.lsi_project.app.user_microservice.security;

import com.lsi_project.app.user_microservice.entities.User;
import com.lsi_project.app.user_microservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Loads the user (based on the wallet address) and their roles for Spring Security.
 */
@Service
public class CostumeUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String walletAddress) throws UsernameNotFoundException {
        User user = userRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with wallet address: " + walletAddress));

        // Map the RoleEnum set from the User entity to Spring Security's GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                .map(roleEnum -> new SimpleGrantedAuthority(roleEnum.name()))
                .collect(Collectors.toList());

        // We use the wallet address as the username (principal)
        // Note: The password field is not used in SIWE authentication, but cannot be null.
        return new org.springframework.security.core.userdetails.User(
                user.getWalletAddress(),
                "", // Password field is empty/mocked as SIWE doesn't use passwords
                authorities
        );
    }
}