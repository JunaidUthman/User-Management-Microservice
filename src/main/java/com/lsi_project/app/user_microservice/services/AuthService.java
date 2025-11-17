package com.lsi_project.app.user_microservice.services;

import com.lsi_project.app.user_microservice.entities.AuthenticationState;
import com.lsi_project.app.user_microservice.entities.User;
import com.lsi_project.app.user_microservice.repositories.AuthenticationStateRepository;
import com.lsi_project.app.user_microservice.repositories.UserRepository;
import com.lsi_project.app.user_microservice.security.CostumeUserService;
import com.lsi_project.app.user_microservice.security.JwtUtil;
import com.lsi_project.app.user_microservice.DTOs.AuthResponse;
import com.lsi_project.app.user_microservice.DTOs.VerificationRequest;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;


/**
 * Service handling Sign-In with Ethereum (SIWE) logic (Nonce generation and Signature verification).
 */
@Service
@AllArgsConstructor
public class AuthService {


    private final UserRepository userRepository;


    private final AuthenticationStateRepository authStateRepository;


    private final JwtUtil jwtUtil;
    private final CostumeUserService costumeUserService;



    private static final String SIGNING_PREFIX = "\u0019Ethereum Signed Message:\n";

    /**
     * Step 1: Generates a unique nonce for the wallet address and stores it.
     * @param walletAddress The address requesting the nonce.
     * @return The generated nonce string.
     */
    @Transactional
    public String generateNonce(String walletAddress) {
        // Normalize wallet address to lowercase for consistency
        String normalizedAddress = walletAddress.toLowerCase();

        // 1. Find or create user
        User user = userRepository.findByWalletAddress(normalizedAddress)
                .orElseThrow(() -> new RuntimeException("User not found. Please ensure the wallet address is registered before proceeding to login."));

        // 2. Generate and store new Nonce
        String nonce = UUID.randomUUID().toString();

        AuthenticationState authState = Optional.ofNullable(user.getAuthState())
                .orElseGet(() -> {// in the creation registration process, i created the AuthenticationState, so this is just a check in case of a probleme
                    AuthenticationState newAuthState = new AuthenticationState();
                    newAuthState.setUser(user);
                    return newAuthState;
                });

        authState.setCurrentNonce(nonce);
        authStateRepository.save(authState);

        return nonce;
    }

    /**
     * Step 3: Verifies the signature against the stored nonce and issues a JWT.
     * @param request Contains walletAddress and signature.
     * @return AuthResponse with JWT on success.
     * @throws RuntimeException if verification fails.
     */
    @Transactional
    public AuthResponse verifySignatureAndLogin(VerificationRequest request) {
        String normalizedAddress = request.getWalletAddress().toLowerCase();

        User user = userRepository.findByWalletAddress(normalizedAddress)
                .orElseThrow(() -> new RuntimeException("User not found. Please initiate nonce request first."));

        AuthenticationState authState = Optional.ofNullable(user.getAuthState())
                .orElseThrow(() -> new RuntimeException("Nonce not found for this user. Please request a new nonce."));

        String nonce = authState.getCurrentNonce();
        if (nonce == null || nonce.isEmpty()) {
            throw new RuntimeException("Nonce expired or missing. Please request a new nonce.");
        }

        // 1. Verify Signature
        boolean isVerified = verifySignature(nonce, request.getSignature(), normalizedAddress);

        if (!isVerified) {
            throw new RuntimeException("Signature verification failed. Invalid signature or message.");
        }

        // 2. Clear Nonce (Mark as used)
        // CRITICAL: Prevent replay attacks by clearing the nonce immediately after successful use.
        authState.setCurrentNonce(null);
        authStateRepository.save(authState);

        // 3. Generate JWT
        UserDetails userDetails = costumeUserService.loadUserByUsername(normalizedAddress);
        String jwt = jwtUtil.generateToken(userDetails);

        return new AuthResponse(jwt, normalizedAddress);
    }

    /**
     * Uses Web3j to recover the signing address from the signature.
     * This is the core cryptographic security check.
     */
    private boolean verifySignature(String message, String signature, String expectedAddress) {
        try {
            // 1. Prepare the message as per EIP-191 standard
            // The message signed by MetaMask client-side is typically prefixed with SIGNING_PREFIX + message length
            String fullMessage = SIGNING_PREFIX + message.length() + message;
            // IMPORTANT: Web3j's utility for key recovery expects the EIP-191 message hash (keccak256 of the prefixed message)
            byte[] messageHash = org.web3j.crypto.Hash.sha3(fullMessage.getBytes());

            // 2. Decode the signature string
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            byte v = signatureBytes[64];
            if (v < 27) {
                v += 27;
            }

            Sign.SignatureData signatureData = new Sign.SignatureData(
                    v,
                    Arrays.copyOfRange(signatureBytes, 0, 32),
                    Arrays.copyOfRange(signatureBytes, 32, 64)
            );

            // 3. Recover the Public Key using the correct Web3j method
            // Sign.signedMessageToKey() takes the message hash and the SignatureData object.
            // This method handles the recovery logic internally using R, S, and V from signatureData.
            BigInteger publicKey = Sign.signedMessageToKey(messageHash, signatureData);

            if (publicKey == null) return false;

            // 4. Convert Public Key to Wallet Address (and normalize)
            String recoveredAddress = Keys.getAddress(publicKey);

            // 5. Comparison
            return recoveredAddress.equalsIgnoreCase(expectedAddress);

        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Signature recovery failed: " + e.getMessage());
            return false;
        }
    }
}