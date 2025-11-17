package com.lsi_project.app.user_microservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "authentication_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "current_nonce", nullable = false)
    private String currentNonce;

    @Column(name = "nonce_generated_at", nullable = false)
    private LocalDateTime nonceGeneratedAt = LocalDateTime.now();

    public AuthenticationState(User user, String nonce) {
        this.user = user;
        this.currentNonce = nonce;
    }
}